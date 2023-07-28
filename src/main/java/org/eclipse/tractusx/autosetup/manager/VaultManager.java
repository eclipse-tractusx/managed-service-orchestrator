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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.model.VaultSecreteRequest;
import org.eclipse.tractusx.autosetup.utility.LogUtil;
import org.eclipse.tractusx.autosetup.vault.proxy.VaultAppManageProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VaultManager {

	private static final String CLIENT_SECRET = "client-secret";
	private static final String V1_SECRET_DATA = "/v1/secret/data/";
	public static final String ENCRYPTIONKEYS = "encryptionkeys";
	public static final String CONTENT = "content";
	public static final String DAPS_CERT = "daps-cert";
	public static final String CERTIFICATE_PRIVATE_KEY = "certificate-private-key";
	private final VaultAppManageProxy vaultManagerProxy;
	private final AutoSetupTriggerManager autoSetupTriggerManager;
	private final OpenSSLClientManager openSSLClientManager;

	@Value("${vault.url}")
	private String valutURL;

	@Value("${vault.token}")
	private String vaulttoken;

	@Value("${vault.timeout}")
	private String vaulttimeout;

	int counter = 0;

	@SneakyThrows
	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "#{${retry.backOffDelay}}"))
	public Map<String, String> uploadKeyandValues(Customer customerDetails, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("VAULT").build();

		try {

			String tenantNameNamespace = triger.getAutosetupTenantName();
			String packageName = tool.getLabel();
			String orgName = customerDetails.getOrganizationName();
			log.info(LogUtil.encode(orgName) + "-" + LogUtil.encode(packageName) + "-Vault creating");

			Map<String, String> tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put(CONTENT, inputData.get("selfsigncertificate"));
			uploadSecrete(tenantNameNamespace, DAPS_CERT, tenantVaultSecret);

			tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put(CONTENT, inputData.get("selfsigncertificateprivatekey"));
			uploadSecrete(tenantNameNamespace, CERTIFICATE_PRIVATE_KEY, tenantVaultSecret);
			
			tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put(CONTENT, inputData.get("keycloakAuthenticationClientSecret"));
			uploadSecrete(tenantNameNamespace, CLIENT_SECRET, tenantVaultSecret);

			String encryptionkeysalias = openSSLClientManager.executeCommand("openssl rand -base64 16");
			tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put(CONTENT, encryptionkeysalias);
			uploadSecrete(tenantNameNamespace, ENCRYPTIONKEYS, tenantVaultSecret);

			inputData.put(DAPS_CERT, DAPS_CERT);
			inputData.put(CERTIFICATE_PRIVATE_KEY, CERTIFICATE_PRIVATE_KEY);
			inputData.put("valuttenantpath", V1_SECRET_DATA + tenantNameNamespace);
			inputData.put("vaulturl", valutURL);
			inputData.put("vaulttoken", vaulttoken);
			inputData.put("vaulttimeout", vaulttimeout);
			inputData.put(CLIENT_SECRET, CLIENT_SECRET);
			inputData.put(ENCRYPTIONKEYS, ENCRYPTIONKEYS);
			inputData.put("certificate-data-plane-private-key", CERTIFICATE_PRIVATE_KEY);
			inputData.put("certificate-data-plane-public-key", CERTIFICATE_PRIVATE_KEY);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			log.info(LogUtil.encode(orgName) + "-" + LogUtil.encode(packageName) + "-Vault created");

		} catch (Exception ex) {

			log.error("VaultManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("VaultManager Oops! We have an exception - " + ex.getMessage());

		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return inputData;
	}

	public void uploadSecrete(String tenantName, String secretePath, Map<String, String> tenantVaultSecret)
			throws URISyntaxException {

		String valutURLwithpath = valutURL + V1_SECRET_DATA + tenantName + "/data/" + secretePath;
		VaultSecreteRequest vaultSecreteRequest = VaultSecreteRequest.builder().data(tenantVaultSecret).build();
		URI url = new URI(valutURLwithpath);
		vaultManagerProxy.uploadKeyandValue(url, vaultSecreteRequest);

	}

	@SneakyThrows
	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "#{${retry.backOffDelay}}"))
	public void deleteAllSecret(SelectedTools tool, Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("VAULT").build();

		try {

			String tenantNameNamespace = triger.getAutosetupTenantName();
			String packageName = tool.getLabel();
			String orgName = triger.getOrganizationName();
			log.info(LogUtil.encode(orgName) + "-" + LogUtil.encode(packageName) + "-Vault deleting");

			deleteSecret(tenantNameNamespace, DAPS_CERT);
			deleteSecret(tenantNameNamespace, CERTIFICATE_PRIVATE_KEY);
			deleteSecret(tenantNameNamespace, ENCRYPTIONKEYS);
			deleteSecret(tenantNameNamespace, CLIENT_SECRET);
			
			log.info(LogUtil.encode(orgName) + "-" + LogUtil.encode(packageName) + "-Vault deleted");

		} catch (Exception ex) {

			log.error("VaultManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("VaultManager Oops! We have an exception - " + ex.getMessage());

		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}
	}

	public void deleteSecret(String tenantName, String secretePath) throws URISyntaxException {

		String valutURLwithpath = valutURL + V1_SECRET_DATA + tenantName+ "/data/" + secretePath;
		URI url = new URI(valutURLwithpath);
		vaultManagerProxy.deleteKeyandValue(url);

	}

}
