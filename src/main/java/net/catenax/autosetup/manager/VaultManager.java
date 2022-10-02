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

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.catenax.autosetup.constant.TriggerStatusEnum;
import net.catenax.autosetup.entity.AutoSetupTriggerDetails;
import net.catenax.autosetup.entity.AutoSetupTriggerEntry;
import net.catenax.autosetup.exception.ServiceException;
import net.catenax.autosetup.model.Customer;
import net.catenax.autosetup.model.SelectedTools;
import net.catenax.autosetup.model.VaultSecreteRequest;
import net.catenax.autosetup.utility.LogUtil;
import net.catenax.autosetup.vault.proxy.VaultAppManageProxy;

@Service
@Slf4j
@RequiredArgsConstructor
public class VaultManager {

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
	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> uploadKeyandValues(Customer customerDetails, SelectedTools tool, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

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
			
			String encryptionkeysalias = openSSLClientManager.executeCommand("openssl rand -base64 16");
			tenantVaultSecret = new HashMap<>();
			tenantVaultSecret.put(CONTENT, encryptionkeysalias);
			uploadSecrete(tenantNameNamespace, ENCRYPTIONKEYS, tenantVaultSecret);

			inputData.remove("selfsigncertificateprivatekey");
			inputData.remove("selfsigncertificate");
			
			inputData.put(DAPS_CERT, DAPS_CERT);
			inputData.put(CERTIFICATE_PRIVATE_KEY, CERTIFICATE_PRIVATE_KEY);
			inputData.put("valuttenantpath", "/v1/secret/data/" + tenantNameNamespace);
			inputData.put("vaulturl", valutURL);
			inputData.put("vaulttoken", vaulttoken);
			inputData.put("vaulttimeout", vaulttimeout);
			inputData.put(ENCRYPTIONKEYS, ENCRYPTIONKEYS);
			inputData.put("certificate-data-plane-private-key", CERTIFICATE_PRIVATE_KEY);
			inputData.put("certificate-data-plane-public-key", CERTIFICATE_PRIVATE_KEY);
			
			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			log.info(LogUtil.encode(orgName) +"-"+  LogUtil.encode(packageName) + "-Vault created");


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
