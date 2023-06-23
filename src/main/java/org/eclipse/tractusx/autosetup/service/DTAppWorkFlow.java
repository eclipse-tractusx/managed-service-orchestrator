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

package org.eclipse.tractusx.autosetup.service;

import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.DT_REGISTRY;

import java.util.Map;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.manager.AppDeleteManager;
import org.eclipse.tractusx.autosetup.manager.DTRegistryManager;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DTAppWorkFlow {

	private final DTRegistryManager dtregistryManager;
	private final AppDeleteManager appDeleteManager;

	public Map<String, String> getWorkFlow(Customer customerDetails, SelectedTools tool, AppActions workflowAction,
			Map<String, String> inputConfiguration, AutoSetupTriggerEntry triger) {

		inputConfiguration.putAll(
				dtregistryManager.managePackage(customerDetails, workflowAction, tool, inputConfiguration, triger));

		Runnable runnable = () -> dtregistryManager.dtRegistryRegistrationInEDC(customerDetails, tool,
				inputConfiguration, triger);

		new Thread(runnable).start();

		return inputConfiguration;
	}

	public void deletePackageWorkFlow(SelectedTools tool, Map<String, String> inputConfiguration,
			AutoSetupTriggerEntry triger) {
		appDeleteManager.deletePackage(DT_REGISTRY, tool, inputConfiguration, triger);
	}
}