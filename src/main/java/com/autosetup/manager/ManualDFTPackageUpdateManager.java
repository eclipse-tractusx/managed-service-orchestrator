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

import static com.autosetup.constant.AppActions.CREATE;
import static com.autosetup.constant.AppNameConstant.DFT_BACKEND;
import static com.autosetup.constant.AppNameConstant.DFT_FRONTEND;
import static com.autosetup.constant.AppNameConstant.POSTGRES_DB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.autosetup.constant.ToolType;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.exception.ServiceException;
import com.autosetup.kubeapp.mapper.AutoSetupTriggerMapper;
import com.autosetup.model.AutoSetupRequest;
import com.autosetup.model.Customer;
import com.autosetup.model.DFTUpdateRequest;
import com.autosetup.model.SelectedTools;
import com.autosetup.service.DFTAppWorkFlow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManualDFTPackageUpdateManager {

	private final KubeAppsPackageManagement appManagement;

	private final AutoSetupTriggerMapper autoSetupTriggerMapper;

	private final DFTAppWorkFlow dftWorkFlow;
	private final EmailManager emailManager;

	@Value("${portal.email.address}")
	private String portalEmail;

	public Map<String, String> manualPackageUpdate(AutoSetupRequest autosetupRequest, DFTUpdateRequest dftUpdateRequest,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry trigger) {

		Map<String, String> map = null;
		try {

			inputConfiguration.put("digital-twins.hostname", dftUpdateRequest.getDigitalTwinUrl());
			inputConfiguration.put("digital-twins.authentication.url", dftUpdateRequest.getDigitalTwinAuthUrl());
			inputConfiguration.put("digital-twins.authentication.clientId", dftUpdateRequest.getDigitalTwinClientId());
			inputConfiguration.put("digital-twins.authentication.clientSecret",
					dftUpdateRequest.getDigitalTwinClientSecret());

			inputConfiguration.put("dftkeycloakurl", dftUpdateRequest.getKeycloakUrl());
			inputConfiguration.put("dftcloakrealm", dftUpdateRequest.getKeycloakRealm());
			inputConfiguration.put("dftbackendkeycloakclientid", dftUpdateRequest.getKeycloakBackendClientId());
			inputConfiguration.put("dftfrontendkeycloakclientid", dftUpdateRequest.getKeycloakFrontendClientId());

			List<Map<String, String>> autosetupResult = autoSetupTriggerMapper
					.fromJsonStrToMap(trigger.getAutosetupResult());

			autosetupResult.forEach(mape -> {
				inputConfiguration.putAll(mape);
			});
			
			List<SelectedTools> dftToollist = autosetupRequest.getSelectedTools().stream()
					.filter(e -> ToolType.DFT.equals(e.getTool())).toList();

			for (SelectedTools element : dftToollist) {

				String packageName = element.getPackageName();

				appManagement.deletePackage(POSTGRES_DB, packageName, inputConfiguration);
				appManagement.deletePackage(DFT_BACKEND, packageName, inputConfiguration);
				appManagement.deletePackage(DFT_FRONTEND, packageName, inputConfiguration);

				// Sleep thread to wait for existing package deletetion
				log.info("Waiting after deleting DFT packages");

				Thread.sleep(15000);

				Customer customer = autosetupRequest.getCustomer();

				map = dftWorkFlow.getWorkFlow(customer, element, CREATE, inputConfiguration, trigger);

				// Send an email
				Map<String, Object> emailContent = new HashMap<>();
				emailContent.put("orgname", customer.getOrganizationName());
				emailContent.put("dftFrontEndUrl", map.get("dftFrontEndUrl"));
				emailContent.put("toemail", customer.getEmail());
				emailContent.put("ccemail", portalEmail);

				emailManager.sendEmail(emailContent, "DFT Application Activited Successfully", "success_activate.html");
				log.info("Email sent successfully");
				// End of email sending code

				log.info("DFT Manual package update successfully!!!!");
			}

		} catch (Exception e) {

			throw new ServiceException("ManualDFT PackageUpdate Oops! We have an exception - " + e.getMessage());
		}
		return map;
	}

}
