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

import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.DFT_BACKEND;
import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.DFT_FRONTEND;
import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.POSTGRES_DB;

import java.util.Map;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.manager.AppDeleteManager;
import org.eclipse.tractusx.autosetup.manager.DFTBackendManager;
import org.eclipse.tractusx.autosetup.manager.DFTFrontendManager;
import org.eclipse.tractusx.autosetup.manager.PostgresDBManager;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DFTAppWorkFlow {

	private final PostgresDBManager postgresManager;
	private final DFTBackendManager dftBackendManager;
	private final DFTFrontendManager dftFrontendManager;

	private final AppDeleteManager appDeleteManager;

	public Map<String, String> getWorkFlow(Customer customerDetails, SelectedTools tool, AppActions workflowAction,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		inputConfiguration.putAll(
				postgresManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));
		inputConfiguration.putAll(
				dftBackendManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));
		inputConfiguration.putAll(
				dftFrontendManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));

		return inputConfiguration;
	}

	public void deletePackageWorkFlow(SelectedTools tool, Map<String, String> inputConfiguration,
			AutoSetupTriggerEntry triger) {

		appDeleteManager.deletePackage(POSTGRES_DB, tool, inputConfiguration, triger);
		appDeleteManager.deletePackage(DFT_BACKEND, tool, inputConfiguration, triger);
		appDeleteManager.deletePackage(DFT_FRONTEND, tool, inputConfiguration, triger);

	}
}
