/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.daps.proxy.PortalRegistrationProxy;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.utility.LogUtil;
import org.keycloak.OAuth2Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PortalRegistrationManager {

	@Value("${dapswrapper.url}")
	private URI dapsRegistrationUrl;
	
	
	
	@Value("${dapswrapper.daps.url}")
	private String dapsurl;
	
	@Value("${dapswrapper.daps.jskurl}")
	private String dapsjsksurl;
	
	@Value("${dapswrapper.daps.token.url}")
	private String dapstokenurl;
	
	
	
	@Value("${dapswrapper.keycloak.clientId}")
	private String clientId;
	
	@Value("${dapswrapper.keycloak.clientSecret}")
	private String clientSecret;
	
	@Value("${dapswrapper.keycloak.tokenURI}")
	private URI tokenURI;
	

	private final AutoSetupTriggerManager autoSetupTriggerManager;
	private final PortalRegistrationProxy portalRegistrationProxy;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> createClient(Customer customerDetails, SelectedTools tool, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("DAPS").build();

		Path file = null;
		try {
			String packageName = tool.getLabel();
			String tenantName = customerDetails.getOrganizationName();

			log.info(LogUtil.encode(tenantName) + "-" + LogUtil.encode(packageName) + "-DAPS package creating");

			file = getTestFile(inputData.get("selfsigncertificate"));

			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");


			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

			body.add("name", customerDetails.getOrganizationName());
			body.add("connectorUrl", dnsNameURLProtocol);
			body.add("status", inputData.get("status"));
			body.add("location", customerDetails.getCountry());
			body.add("providerBpn", inputData.get("bpnNumber"));
			body.add("certificate", new FileSystemResource(file.toFile()));
			Map<String, String> header = new HashMap<>();
			header.put("Authorization", "Bearer " + getKeycloakToken());

			portalRegistrationProxy.registerClient(URI.create("/api/administration/connectors/managed"), header, body);

			inputData.put("dapsurl", dapsurl);
			inputData.put("dapsjsksurl", dapsjsksurl);
			inputData.put("dapstokenurl", dapstokenurl);

			log.info(LogUtil.encode(tenantName) + "-" + LogUtil.encode(packageName) + "-DAPS package created");
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			
		} catch (Exception ex) {

			log.error("DAPsWrapperManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("DAPsWrapperManager Oops! We have an exception - " + ex.getMessage());

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
	public String getKeycloakToken() {
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.CLIENT_CREDENTIALS);
		body.add(OAuth2Constants.CLIENT_ID, clientId);
		body.add(OAuth2Constants.CLIENT_SECRET, clientSecret);
		var resultBody = portalRegistrationProxy.readAuthToken(tokenURI, body);

		if (resultBody != null) {
			return resultBody.getAccessToken();
		}
		return null;

	}

	public static Path getTestFile(String str) throws IOException {
		Path testFile = Files.createTempFile("test-file1", ".crt");
		Files.write(testFile, str.getBytes());
		return testFile;
	}

}
