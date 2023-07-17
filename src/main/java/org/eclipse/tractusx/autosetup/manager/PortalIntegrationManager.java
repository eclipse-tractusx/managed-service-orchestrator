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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.portal.model.ClientInfo;
import org.eclipse.tractusx.autosetup.portal.model.ServiceInstanceResultRequest;
import org.eclipse.tractusx.autosetup.portal.model.ServiceInstanceResultResponse;
import org.eclipse.tractusx.autosetup.portal.model.TechnicalUserInfo;
import org.eclipse.tractusx.autosetup.portal.proxy.PortalIntegrationProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortalIntegrationManager {

	private final PortalIntegrationProxy portalIntegrationProxy;

	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Value("${portal.url}")
	private URI portalUrl;

	@Value("${portal.keycloak.clientId}")
	private String clientId;

	@Value("${portal.keycloak.clientSecret}")
	private String clientSecret;

	@Value("${portal.keycloak.tokenURI}")
	private URI tokenURI;

	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> postServiceInstanceResultAndGetTenantSpecs(Customer customerDetails, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("PostServiceInstanceResultAndGetTenantSpecs").build();
		ServiceInstanceResultResponse serviceInstanceResultResponse = null;
		try {

			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");
			String subscriptionId = inputData.get("subscriptionId");

			String applicationURL = dnsNameURLProtocol + "://" + dnsName;
			inputData.put("applicationURL", applicationURL);

			Map<String, String> header = new HashMap<>();
			header.put("Authorization", "Bearer " + getKeycloakToken());

			ServiceInstanceResultRequest serviceInstanceResultRequest = ServiceInstanceResultRequest.builder()
					.requestId(subscriptionId).offerUrl(applicationURL).build();
			
			if ("app".equalsIgnoreCase(tool.getType()))
				serviceInstanceResultResponse = portalIntegrationProxy.postAppInstanceResultAndGetTenantSpecs(portalUrl,
						header, serviceInstanceResultRequest);
			else
				serviceInstanceResultResponse = portalIntegrationProxy
						.postServiceInstanceResultAndGetTenantSpecs(portalUrl, header, serviceInstanceResultRequest);

			if (serviceInstanceResultResponse != null) {

				TechnicalUserInfo technicalUserInfo = serviceInstanceResultResponse.getTechnicalUserInfo();
				if (technicalUserInfo != null) {
					inputData.put("keycloakAuthenticationClientId", technicalUserInfo.getTechnicalClientId());
					inputData.put("keycloakAuthenticationClientSecret", technicalUserInfo.getTechnicalUserSecret());
				}

				ClientInfo clientInfo = serviceInstanceResultResponse.getClientInfo();
				if (clientInfo != null) {
					inputData.put("keycloakResourceClient", clientInfo.getClientId());
				}
			} else {
				log.error("Error in request process with portal");
			}
		} catch (Exception ex) {

			log.error("PortalIntegrationManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			if (serviceInstanceResultResponse != null) {
				String msg = "PortalIntegrationManager failed with details:" + serviceInstanceResultResponse.toJsonString();
				log.error(msg);
				autoSetupTriggerDetails.setRemark(msg);
			}
			else
				autoSetupTriggerDetails.setRemark(ex.getMessage());

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());

			throw new ServiceException("PortalIntegrationManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}
		return inputData;
	}

	@SneakyThrows
	private String getKeycloakToken() {

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "client_credentials");
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		var resultBody = portalIntegrationProxy.readAuthToken(tokenURI, body);

		if (resultBody != null) {
			return resultBody.getAccessToken();
		}
		return null;

	}

}
