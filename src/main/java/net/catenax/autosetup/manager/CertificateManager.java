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

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import net.catenax.autosetup.utility.Certutil;
import net.catenax.autosetup.utility.LogUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class CertificateManager {

	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@SneakyThrows
	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> createCertificate(Customer customerDetails, SelectedTools tool, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("CERTIFICATE").build();

		try {

			String packageName = tool.getLabel();
			String tenantName = customerDetails.getOrganizationName();
			
			log.info(LogUtil.encode(tenantName) +"-"+ LogUtil.encode(packageName) + "-certificate creating");

			String bpnNumber=inputData.get("bpnNumber");
			
			String c = Optional.ofNullable(customerDetails.getCountry()).map(r -> r).orElse("DE");
			String st = Optional.ofNullable(customerDetails.getState()).map(r -> r).orElse("BE");
			String l = Optional.ofNullable(customerDetails.getCity()).map(r -> r).orElse("Berline");

			String params = String.format("O=%s, OU=%s, C=%s, ST=%s, L=%s, CN=%s", tenantName,
					bpnNumber, c, st, l, "www." + tenantName + ".com");

			Certutil.CertKeyPair certificateDetails = Certutil.generateSelfSignedCertificateSecret(params, null, null);
			X509Certificate certificate = certificateDetails.certificate();
			String clientId = Certutil.getClientId(certificate);

			outputData.put("dapsclientid", clientId);
			outputData.put("selfsigncertificate", Certutil.getAsString(certificate));
			outputData.put("selfsigncertificateprivatekey",
					Certutil.getAsString(certificateDetails.keyPair().getPrivate()));

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());
			log.info(LogUtil.encode(tenantName) +"-"+  LogUtil.encode(packageName) + "-certificate created");

		} catch (Exception ex) {

			log.error("CertificateManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("CertificateManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return outputData;

	}

}
