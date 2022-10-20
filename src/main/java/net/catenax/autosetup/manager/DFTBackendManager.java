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

import static net.catenax.autosetup.constant.AppNameConstant.DFT_BACKEND;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.autosetup.constant.AppActions;
import net.catenax.autosetup.constant.TriggerStatusEnum;
import net.catenax.autosetup.entity.AutoSetupTriggerDetails;
import net.catenax.autosetup.entity.AutoSetupTriggerEntry;
import net.catenax.autosetup.exception.ServiceException;
import net.catenax.autosetup.model.Customer;
import net.catenax.autosetup.model.SelectedTools;
import net.catenax.autosetup.utility.PasswordGenerator;

@Slf4j
@Service
@RequiredArgsConstructor
public class DFTBackendManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	private final PortalIntegrationManager portalIntegrationManager;

	@Value("${manual.update}")
	private boolean manualUpdate;
	
	@Value("${dft.portal.pool}")
	private String dftprotalpool;
	
	@Value("${dft.protal.backend}")
	private String dftprotalbackend;
	
	@Value("${dft.portal.clientid}")
	private String dftportalclientid;
	
	@Value("${dft.portal.clientSecret}")
	private String dftportalclientSecret;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(DFT_BACKEND.name()).build();
		try {
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			inputData.put("manufacturerId", inputData.get("bpnNumber"));

			String backendurl = dnsNameURLProtocol + "://" + dnsName + "/dftbackend/api";
			String dftfrontend = dnsNameURLProtocol + "://" + dnsName;

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);

			inputData.put("dftBackEndUrl", backendurl);
			inputData.put("dftBackEndApiKey", generateRandomPassword);
			inputData.put("dftBackEndApiKeyHeader", "API_KEY");
			inputData.put("dftFrontEndUrl", dftfrontend);
			
			inputData.put("dftprotalpool", dftprotalpool);
			inputData.put("dftprotalbackend", dftprotalbackend);
			inputData.put("dftportalclientid", dftportalclientid);
			inputData.put("dftportalclientSecret", dftportalclientSecret);
			
			if (!manualUpdate) {
				Map<String, String> portalDetails = portalIntegrationManager
						.postServiceInstanceResultAndGetTenantSpecs(inputData);
				inputData.putAll(portalDetails);
			}

			String packageName = tool.getLabel();

			String dftDb = "jdbc:postgresql://" + packageName + "-postgresdb-postgresql:5432/postgres";
			inputData.put("dftdatabaseurl", dftDb);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(DFT_BACKEND, packageName, inputData);
			else
				appManagement.updatePackage(DFT_BACKEND, packageName, inputData);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("DftBackendManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("DftBackendManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}
		return inputData;
	}
}