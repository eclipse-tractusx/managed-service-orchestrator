/********************************************************************************
 * Copyright (c) 2022 T-Systems International GmbH
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.EDC_CONTROLPLANE;
import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.EDC_DATAPLANE;
import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.POSTGRES_DB;

import java.util.Map;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.manager.AppDeleteManager;
import org.eclipse.tractusx.autosetup.manager.CertificateManager;
import org.eclipse.tractusx.autosetup.manager.PortalRegistrationManager;
import org.eclipse.tractusx.autosetup.manager.EDCControlplaneManager;
import org.eclipse.tractusx.autosetup.manager.EDCDataplaneManager;
import org.eclipse.tractusx.autosetup.manager.PostgresDBManager;
import org.eclipse.tractusx.autosetup.manager.VaultManager;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EDCConnectorWorkFlow {

	private final CertificateManager certificateManager;
	private final PortalRegistrationManager daPsWrapperManager;
	private final VaultManager vaultManager;
	private final PostgresDBManager postgresManager;
	private final EDCControlplaneManager edcControlplaneManager;
	private final EDCDataplaneManager edcDataplaneManager;

	private final AppDeleteManager appDeleteManager;

	public Map<String, String> getWorkFlow(Customer customerDetails, SelectedTools tool, AppActions workflowAction,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		inputConfiguration
				.putAll(certificateManager.createCertificate(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(daPsWrapperManager.createClient(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(vaultManager.uploadKeyandValues(customerDetails, tool, inputConfiguration, triger));
		inputConfiguration.putAll(
				postgresManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));
		inputConfiguration.putAll(edcControlplaneManager.managePackage(customerDetails, workflowAction, tool,
				inputConfiguration, triger));
		inputConfiguration.putAll(
				edcDataplaneManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));

		return inputConfiguration;
	}

	public void deletePackageWorkFlow(SelectedTools tool, Map<String, String> inputConfiguration,
			AutoSetupTriggerEntry triger) {

		appDeleteManager.deletePackage(POSTGRES_DB, tool, inputConfiguration, triger);
		appDeleteManager.deletePackage(EDC_CONTROLPLANE, tool, inputConfiguration, triger);
		appDeleteManager.deletePackage(EDC_DATAPLANE, tool, inputConfiguration, triger);
	}
}
