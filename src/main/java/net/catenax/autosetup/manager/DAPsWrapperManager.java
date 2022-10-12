/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.autosetup.manager;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import net.catenax.autosetup.constant.TriggerStatusEnum;
import net.catenax.autosetup.daps.proxy.DAPsWrapperProxy;
import net.catenax.autosetup.entity.AutoSetupTriggerDetails;
import net.catenax.autosetup.entity.AutoSetupTriggerEntry;
import net.catenax.autosetup.exception.ServiceException;
import net.catenax.autosetup.model.Customer;
import net.catenax.autosetup.model.SelectedTools;
import net.catenax.autosetup.utility.LogUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class DAPsWrapperManager {

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
	private final DAPsWrapperProxy dapsWrapperProxy;

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

			String referringConnector = dnsNameURLProtocol + "://" + inputData.get("dnsName") + "/"
					+ inputData.get("bpnNumber");

			String targetNamespace = inputData.get("targetNamespace");

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

			body.add("clientName", targetNamespace + "-" + packageName);
			body.add("referringConnector", referringConnector);
			body.add("file", new FileSystemResource(file.toFile()));
			Map<String, String> header = new HashMap<String, String>();
			header.put("Authorization", "Bearer " + getKeycloakToken());

			dapsWrapperProxy.registerClient(dapsRegistrationUrl, header, body);

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
				e.printStackTrace();
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
		var resultBody = dapsWrapperProxy.readAuthToken(tokenURI, body);

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
