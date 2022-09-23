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

package com.autosetup.manager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import com.autosetup.constant.TriggerStatusEnum;
import com.autosetup.entity.AutoSetupTriggerDetails;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.exception.ServiceException;
import com.autosetup.model.Customer;
import com.autosetup.model.SelectedTools;
import com.autosetup.model.VaultSecreteRequest;
import com.autosetup.proxy.vault.VaultAppManageProxy;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class VaultManager {

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
	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> uploadKeyandValues(Customer customerDetails, SelectedTools tool, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("VAULT").build();

		try {

			String tenantNameNamespace = triger.getAutosetupTenantName();
			String packageName = tool.getPackageName();
			String orgName = customerDetails.getOrganizationName();
			log.info(orgName +"-"+  packageName + "-Vault creating");

			Map<String, String> tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put("content", inputData.get("selfsigncertificate"));
			uploadSecrete(tenantNameNamespace, "daps-cert", tenantVaultSecret);

			tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put("content", inputData.get("selfsigncertificateprivatekey"));
			uploadSecrete(tenantNameNamespace, "certificate-private-key", tenantVaultSecret);
			
			String encryptionkeysalias = openSSLClientManager.executeCommand("openssl rand -base64 16");
			tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put("content", encryptionkeysalias);
			uploadSecrete(tenantNameNamespace, "encryptionkeys", tenantVaultSecret);

			inputData.remove("selfsigncertificateprivatekey");
			inputData.remove("selfsigncertificate");
			
			inputData.put("daps-cert", "daps-cert");
			inputData.put("certificate-private-key", "certificate-private-key");
			inputData.put("valuttenantpath", "/v1/secret/data/" + tenantNameNamespace);
			inputData.put("vaulturl", valutURL);
			inputData.put("vaulttoken", vaulttoken);
			inputData.put("vaulttimeout", vaulttimeout);
			inputData.put("encryptionkeys", "encryptionkeys");
			inputData.put("certificate-data-plane-private-key", "certificate-private-key");
			inputData.put("certificate-data-plane-public-key", "certificate-private-key");
			
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			log.info(orgName +"-"+  packageName + "-Vault created");


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

		String valutURLwithpath = valutURL + "/v1/secret/data/" + tenantName + "/data/" + secretePath;
		VaultSecreteRequest vaultSecreteRequest = VaultSecreteRequest.builder().data(tenantVaultSecret).build();
		URI url = new URI(valutURLwithpath);
		vaultManagerProxy.uploadKeyandValue(url, vaultSecreteRequest);

	}

}
