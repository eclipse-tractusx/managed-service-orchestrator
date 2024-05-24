/********************************************************************************
 * Copyright (c) 2022, 2023 T-Systems International GmbH
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.autosetup.manager;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.portal.proxy.PortalIntegrationProxy;
import org.eclipse.tractusx.autosetup.utility.JsonObjectProcessingUtility;
import org.eclipse.tractusx.autosetup.utility.KeyCloakTokenProxyUtitlity;
import org.eclipse.tractusx.autosetup.utility.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectorRegistrationManager {

	private static final String SUBSCRIPTION_ID = "subscriptionId";

	private static final String ACTIVE = "ACTIVE";

	@Value("${connectorregister.url}")
	private URI connectorRegistrationUrl;

	@Value("${connectorregister.keycloak.clientId}")
	private String clientId;

	@Value("${connectorregister.keycloak.clientSecret}")
	private String clientSecret;

	@Value("${connectorregister.keycloak.tokenURI}")
	private URI tokenURI;

	private final AutoSetupTriggerManager autoSetupTriggerManager;
	private final PortalIntegrationProxy portalIntegrationProxy;
	private final KeyCloakTokenProxyUtitlity keyCloakTokenProxyUtitlity;

	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "#{${retry.backOffDelay}}"))
	public Map<String, String> registerConnector(Customer customerDetails, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("CONNECTOR-REGISTER").build();

		Path file = null;
		try {
			String packageName = tool.getLabel();
			String tenantName = customerDetails.getOrganizationName();

			log.info(LogUtil.encode(tenantName) + "-" + LogUtil.encode(packageName)
					+ "-CONNECTOR-REGISTER package creating");

			file = getTestFile(inputData.get("selfsigncertificate"));
			String subscriptionIdVal = inputData.get(SUBSCRIPTION_ID);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			String tenantNameNamespace = triger.getAutosetupTenantName();
			body.add("name", tenantNameNamespace);
			body.add("connectorUrl", inputData.get("controlPlaneEndpoint"));
			body.add("location", customerDetails.getCountry());
			body.add(SUBSCRIPTION_ID, subscriptionIdVal);
			Map<String, String> header = new HashMap<>();
			header.put("Authorization",
					"Bearer " + keyCloakTokenProxyUtitlity.getKeycloakToken(clientId, clientSecret, tokenURI));

			String connectorId = checkSubcriptionHaveConnectorRegister(header, subscriptionIdVal);

			if (StringUtils.isNotBlank(connectorId)) {
				Map<String, String> updateBody = new HashMap<>();
				updateBody.put("connectorUrl", inputData.get("controlPlaneEndpoint"));
				portalIntegrationProxy.updateRegisterConnectorUrl(connectorRegistrationUrl, header, updateBody);
			} else {
				connectorId = portalIntegrationProxy.manageConnector(connectorRegistrationUrl, header, body);
			}

			log.info(LogUtil.encode(tenantName) + "-" + LogUtil.encode(packageName)
					+ "-CONNECTOR-REGISTER package created");
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			inputData.put("connectorstatus", ACTIVE);
			inputData.put("connectorId", connectorId.replace("\"", ""));
			autoSetupTriggerDetails.setRemark("connectorId:" + connectorId);

			inputData.remove("selfsigncertificateprivatekey");
			inputData.remove("selfsigncertificate");

		} catch (FeignException e) {

			log.error("ConnectorregisterManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);
			log.error("RequestBody: " + e.request());
			log.error("ResponseBody: " + e.contentUTF8());

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(e.contentUTF8());
			if (e.toString().contains("FeignException$Conflict") || e.toString().contains("409 Conflict")) {
				log.warn(
						"Skipping connector registration process and also continue with remaining steps of autosetup process");
			} else
				throw new ServiceException("ConnectorregisterManager Oops! We have an exception - " + e.contentUTF8());

		} catch (Exception ex) {

			log.error("ConnectorregisterManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("ConnectorregisterManager Oops! We have an exception - " + ex.getMessage());

		} finally {
			try {
				Files.deleteIfExists(file);
			} catch (IOException e) {

				log.error("Error in deleting cerificate file");
			}
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return inputData;

	}

	@SneakyThrows
	private String checkSubcriptionHaveConnectorRegister(Map<String, String> header, String subscriptionId) {

		try {
			JsonNode subcriptionWithConnectors = portalIntegrationProxy
					.getSubcriptionWithConnectors(connectorRegistrationUrl, header, true);

			if (subcriptionWithConnectors != null && subcriptionWithConnectors.isArray()) {
				for (JsonNode jsonNode : subcriptionWithConnectors) {

					String remoteSubscriptionId = JsonObjectProcessingUtility.getValueFromJsonNode(jsonNode,
							SUBSCRIPTION_ID);

					if (subscriptionId.equalsIgnoreCase(remoteSubscriptionId)) {

						JsonNode connectorIds = JsonObjectProcessingUtility.getArrayNodeFromJsonNode(jsonNode,
								"connectorIds");

						if (connectorIds != null && connectorIds.isArray() && connectorIds.size() > 0)
							return connectorIds.get(0).asText();
					}
				}
			}

		} catch (Exception e) {
			log.error("Error in checkSubcriptionHaveConnectorRegister or not " + e.getMessage());
		}

		return null;
	}

	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "#{${retry.backOffDelay}}"))
	public Map<String, String> deleteConnector(SelectedTools tool, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("CONNECTOR-DELETE").build();

		try {
			String packageName = tool.getLabel();
			String orgName = triger.getOrganizationName();

			String connectorId = inputData.get("connectorId");
			if (!StringUtils.isBlank(connectorId)) {
				log.info(LogUtil.encode(orgName) + "-" + LogUtil.encode(packageName) + "-CONNECTOR-DELETE deleting");

				Map<String, String> header = new HashMap<>();
				header.put("Authorization",
						"Bearer " + keyCloakTokenProxyUtitlity.getKeycloakToken(clientId, clientSecret, tokenURI));

				autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
				portalIntegrationProxy.deleteConnector(connectorRegistrationUrl, header, connectorId);

				log.info(LogUtil.encode(orgName) + "-" + LogUtil.encode(packageName) + "-CONNECTOR-DELETE  deleted");

			} else
				log.error("Connector Id not found in autosetup result to delete connector from portal");

		} catch (Exception ex) {

			log.error("connectorregisterManager DELETE failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException(
					"ConnectorregisterManager DELETE Oops! We have an exception - " + ex.getMessage());

		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return inputData;

	}

	public static Path getTestFile(String str) throws IOException {
		Path testFile = Files.createTempFile("test-file1", ".crt");
		Files.write(testFile, str.getBytes());
		return testFile;
	}

}
