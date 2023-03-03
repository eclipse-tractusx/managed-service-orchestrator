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

import static org.eclipse.tractusx.autosetup.constant.AppActions.CREATE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.tractusx.autosetup.entity.AppServiceCatalogAndCustomerMapping;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.mapper.AutoSetupTriggerMapper;
import org.eclipse.tractusx.autosetup.model.AutoSetupRequest;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.DFTUpdateRequest;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.service.DFTAppWorkFlow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManualDFTPackageUpdateManager {

	private final AutoSetupTriggerMapper autoSetupTriggerMapper;

	private final DFTAppWorkFlow dftWorkFlow;
	private final EmailManager emailManager;

	@Value("${portal.email.address}")
	private String portalEmail;

	ObjectMapper mapper = new ObjectMapper();

	public Map<String, String> manualPackageUpdate(AutoSetupRequest autosetupRequest, DFTUpdateRequest dftUpdateRequest,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry trigger,
			List<AppServiceCatalogAndCustomerMapping> appCatalogDetails) {

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

			inputConfiguration.put("dftportalclientid", dftUpdateRequest.getDftportalclientid());
			inputConfiguration.put("dftportalclientSecret", dftUpdateRequest.getDftportalclientSecret());

			
			List<Map<String, String>> autosetupResult = autoSetupTriggerMapper
					.fromJsonStrToMap(trigger.getAutosetupResult());

			autosetupResult.forEach(inputConfiguration::putAll);

			for (AppServiceCatalogAndCustomerMapping appServiceCatalog : appCatalogDetails) {

				List<SelectedTools> selectedTools = getToolInfo(appServiceCatalog);

				for (SelectedTools selectedTool : selectedTools) {
				String label = selectedTool.getLabel();
				selectedTool.setLabel("dft-" + label);

				dftWorkFlow.deletePackageWorkFlow(selectedTool, inputConfiguration, trigger);

				// Sleep thread to wait for existing package deletetion
				log.info("Waiting after deleting DFT packages");

				Thread.sleep(15000);

				Customer customer = autosetupRequest.getCustomer();

				map = dftWorkFlow.getWorkFlow(customer, selectedTool, CREATE, inputConfiguration, trigger);

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
			}

		} catch (Exception e) {
			Thread.currentThread().interrupt();
			throw new ServiceException("ManualDFT PackageUpdate Oops! We have an exception - " + e.getMessage());
		}
		return map;
	}

	private List<SelectedTools> getToolInfo(AppServiceCatalogAndCustomerMapping appCatalog) {

		try {
			String jsonStr = appCatalog.getServiceCatalog().getServiceTools();

			if (jsonStr != null && !jsonStr.isEmpty()) {
				return mapper.readValue(jsonStr, new TypeReference<List<SelectedTools>>() {
				});
			}
		} catch (Exception e) {
			log.error("Error in parsing selected tools list");
		}
		return List.of();
	}
}
