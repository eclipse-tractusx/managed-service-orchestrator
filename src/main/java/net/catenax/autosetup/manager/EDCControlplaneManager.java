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

import static net.catenax.autosetup.constant.AppNameConstant.EDC_CONTROLPLANE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
public class EDCControlplaneManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(EDC_CONTROLPLANE.name())
				.build();
		try {
			String packageName = tool.getLabel();

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			String controlplaneurl = dnsNameURLProtocol + "://" + dnsName;

			inputData.put("dataPlanePublicUrl",
					dnsNameURLProtocol + "://" + packageName + "-edcdataplane-edc-dataplane:8185/api/public");
			
			String localControlplane = dnsNameURLProtocol + "://" + packageName
					+ "-edccontrolplane-edc-controlplane:8182/validation/token";

			outputData.put("controlPlaneValidationEndpoint", localControlplane);

			outputData.put("controlPlaneEndpoint", controlplaneurl);
			outputData.put("controlPlaneDataEndpoint", controlplaneurl + "/data");
			outputData.put("edcApiKey", "X-Api-Key");
			outputData.put("edcApiKeyValue", generateRandomPassword);
			outputData.put("controlPlaneIdsEndpoint", controlplaneurl + "/api/v1/ids/data");

			inputData.putAll(outputData);

			String dftAddress = dnsNameURLProtocol + "://" + dnsName + "/dftbackend/api";
			inputData.put("dftAddress", dftAddress);

			String edcDb = "jdbc:postgresql://" + packageName + "-postgresdb-postgresql:5432/postgres";
			inputData.put("edcdatabaseurl", edcDb);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(EDC_CONTROLPLANE, packageName, inputData);
			else
				appManagement.updatePackage(EDC_CONTROLPLANE, packageName, inputData);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("EDCControlplaneMaanger failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("EDCControlplaneMaanger Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return outputData;
	}

}
