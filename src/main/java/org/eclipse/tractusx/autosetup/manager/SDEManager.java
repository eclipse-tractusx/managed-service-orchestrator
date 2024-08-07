/********************************************************************************
 * Copyright (c) 2022,2024 T-Systems International GmbH
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.SDE;

import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.constant.EmailConfigurationProperty;
import org.eclipse.tractusx.autosetup.constant.SDEConfigurationProperty;
import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.utility.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SDEManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Value("${managed.dt-registry:true}")
	private boolean managedDtRegistry;

	@Value("${manual.update:false}")
	private boolean manualUpdate;

	private final SDEConfigurationProperty sDEConfigurationProperty;
	private final EmailConfigurationProperty emailConfigurationProperty;

	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "#{${retry.backOffDelay}}"))
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(SDE.name()).build();
		try {
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			inputData.put("manufacturerId", inputData.get("bpnNumber"));

			String backendurl = dnsNameURLProtocol + "://" + dnsName + "/backend/api";
			String sdefrontend = dnsNameURLProtocol + "://" + dnsName;

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);

			inputData.put("sdeBackEndUrl", backendurl);
			inputData.put("sdeBackEndApiKey", generateRandomPassword);
			inputData.put("sdeBackEndApiKeyHeader", "API_KEY");
			inputData.put("sdeFrontEndUrl", sdefrontend);
			inputData.put("database", "sde");

			String keycloakAuthenticationClientId = sDEConfigurationProperty.getKeycloakTechnicalClientid();
			String keycloakAuthenticationClientSecret = sDEConfigurationProperty.getKeycloakTechnicalClientsecret();

			inputData.put("sde.digital-twins.authentication.url", sDEConfigurationProperty.getKeycloakTokenUrl());

			if (!manualUpdate) {
				
				keycloakAuthenticationClientId = inputData.get("keycloakAuthenticationClientId");
				keycloakAuthenticationClientSecret = inputData.get("keycloakAuthenticationClientSecret");

				inputData.put("digital-twins.authentication.clientId", keycloakAuthenticationClientId);
				inputData.put("digital-twins.authentication.clientSecret", keycloakAuthenticationClientSecret);

				inputData.put("sdebackendkeycloakclientid", inputData.get("keycloakResourceClient"));
				inputData.put("sdefrontendkeycloakclientid", inputData.get("keycloakResourceClient"));
			}

			if (managedDtRegistry) {
				inputData.put("sde.digital-twins.hostname", inputData.get("dtregistryUrl"));
			} else {
				inputData.put("sde.digital-twins.hostname", sDEConfigurationProperty.getDigitalTwinsHostname());
			}

			
			inputData.put("sde.resourceServerIssuer", sDEConfigurationProperty.getResourceServerIssuer());
			inputData.put("sde.keycloak.auth", sDEConfigurationProperty.getKeycloakAuth());
			inputData.put("sde.keycloak.realm", sDEConfigurationProperty.getKeycloakRealm());
			inputData.put("sde.keycloak.tokenUrl", sDEConfigurationProperty.getKeycloakTokenUrl());
			inputData.put("sde.keycloak.technical.clientid", keycloakAuthenticationClientId);
			inputData.put("sde.keycloak.technical.clientsecret", keycloakAuthenticationClientSecret);

			inputData.put("sde.partner.pool.hostname", sDEConfigurationProperty.getPartnerPoolHostname());
			inputData.put("sde.portal.backend.hostname", sDEConfigurationProperty.getPortalBackendHostname());
			inputData.put("sde.bpndiscovery.hostname", sDEConfigurationProperty.getBpndiscoveryHostname());
			inputData.put("sde.policy.hub.hostname", sDEConfigurationProperty.getPolicyhubHostname());

			inputData.put("sftpHost", "defaulthost");
			inputData.put("sftpPort", "22");
			inputData.put("sftpUsername", "defaultuser");
			inputData.put("sftpPassword", "defaultpass");
			inputData.put("sftpKey", "");

			inputData.put("emailUsername", emailConfigurationProperty.getUsername());
			inputData.put("emailPassword", emailConfigurationProperty.getPassword());
			inputData.put("emailHost", emailConfigurationProperty.getHost());
			inputData.put("emailPort", emailConfigurationProperty.getPort());
			inputData.put("emailTo", customerDetails.getEmail());
			inputData.put("emailCC", emailConfigurationProperty.getReplytoAddress());
			inputData.put("emailFrom", customerDetails.getOrganizationName() + " SDE notification<noreply@sde.com>");
			inputData.put("emailReply", emailConfigurationProperty.getReplytoAddress());

			inputData.put("bpdm.provider.edc.dsp.api", sDEConfigurationProperty.getBpdmProviderEdcDspApi());
			inputData.put("bpdm.provider.bpnl", sDEConfigurationProperty.getBpdmProviderBpnl());

			String packageName = tool.getLabel();

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(SDE, packageName, inputData);
			else
				appManagement.updatePackage(SDE, packageName, inputData);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("SDEManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());

			throw new ServiceException("SDEManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}
		return inputData;
	}
}