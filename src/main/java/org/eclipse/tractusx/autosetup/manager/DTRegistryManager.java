/********************************************************************************
 * Copyright (c) 2023 T-Systems International GmbH
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.DT_REGISTRY;

import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.constant.SDEConfigurationProperty;
import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DTRegistryManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	private final SDEConfigurationProperty sDEConfigurationProperty;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(DT_REGISTRY.name()).build();
		try {
			String packageName = tool.getLabel();

			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			String dtregistryUrl = dnsNameURLProtocol + "://" + dnsName + ""
					+ sDEConfigurationProperty.getDtregistryUrlPrefix();

			inputData.put("rgdatabase", "registry");
			inputData.put("rgdbpass", "admin@123");
			inputData.put("rgusername", "catenax");
			inputData.put("idpClientId", sDEConfigurationProperty.getDtregistryidpClientId());
			inputData.put("idpIssuerUri", sDEConfigurationProperty.getResourceServerIssuer());
			inputData.put("tenantId", "bpn");
			inputData.put("dtregistryUrlPrefix", sDEConfigurationProperty.getDtregistryUrlPrefix());

			inputData.put("sde.digital-twins.hostname", dtregistryUrl);
			inputData.put("sde.digital-twins.authentication.url",
					sDEConfigurationProperty.getDigitalTwinsAuthenticationUrl());
			inputData.put("dtregistryUrl", dtregistryUrl);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(DT_REGISTRY, packageName, inputData);
			else
				appManagement.updatePackage(DT_REGISTRY, packageName, inputData);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("DTRegistryManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("DTRegistryManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return inputData;
	}

}
