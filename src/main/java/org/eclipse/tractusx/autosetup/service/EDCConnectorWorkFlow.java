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

package org.eclipse.tractusx.autosetup.service;

import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.EDC_CONNECTOR;

import java.util.Map;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.manager.AppDeleteManager;
import org.eclipse.tractusx.autosetup.manager.CertificateManager;
import org.eclipse.tractusx.autosetup.manager.ConnectorRegistrationManager;
import org.eclipse.tractusx.autosetup.manager.PortalIntegrationManager;
import org.eclipse.tractusx.autosetup.manager.TestConnectorServiceManager;
import org.eclipse.tractusx.autosetup.manager.TractusConnectorManager;
import org.eclipse.tractusx.autosetup.manager.VaultManager;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EDCConnectorWorkFlow {

	private final CertificateManager certificateManager;
	private final VaultManager vaultManager;
	private final TractusConnectorManager tractusConnectorManager;
	private final ConnectorRegistrationManager connectorRegistrationManager;
	private final TestConnectorServiceManager testConnectorServiceManager;

	private final AppDeleteManager appDeleteManager;

	private final PortalIntegrationManager portalIntegrationManager;

	public Map<String, String> getWorkFlow(Customer customerDetails, SelectedTools tool, AppActions workflowAction,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		portalIntegrationManager.postServiceInstanceResultAndGetTenantSpecs(customerDetails, tool, inputConfiguration,
				triger);

		inputConfiguration
				.putAll(certificateManager.createCertificate(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(vaultManager.uploadKeyandValues(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(tractusConnectorManager.managePackage(customerDetails, workflowAction, tool,
				inputConfiguration, triger));
		inputConfiguration.putAll(
				connectorRegistrationManager.registerConnector(customerDetails, tool, inputConfiguration, triger));

		try {
			inputConfiguration.putAll(testConnectorServiceManager
					.verifyConnectorTestingThroughTestService(customerDetails, inputConfiguration, triger));
		} catch (ServiceException ex) {
			log.warn(ex.getMessage());
		}

		return inputConfiguration;
	}

	public void deletePackageWorkFlow(SelectedTools tool, Map<String, String> inputConfiguration,
			AutoSetupTriggerEntry triger) {

		vaultManager.deleteAllSecret(tool, inputConfiguration, triger);

		try {
			connectorRegistrationManager.deleteConnector(tool, inputConfiguration, triger);
		} catch (ServiceException ex) {
			log.error(ex.getMessage());
		}

		appDeleteManager.deletePackage(EDC_CONNECTOR, tool, inputConfiguration, triger);
	}

}
