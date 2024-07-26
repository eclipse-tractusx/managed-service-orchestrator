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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.constant.EmailConfigurationProperty;
import org.eclipse.tractusx.autosetup.constant.SDEConfigurationProperty;
import org.eclipse.tractusx.autosetup.constant.ToolType;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SDEManagerTest {

	@InjectMocks
	private SDEManager sdeManager;

	@Mock
	private KubeAppsPackageManagement appManagement;

	@Mock
	private PortalIntegrationManager portalIntegrationManager;

	@Mock
	private AutoSetupTriggerManager autoSetupTriggerManager;

	@Mock
	private SDEConfigurationProperty sDEConfigurationProperty;
	
	@Mock
	private EmailConfigurationProperty emailConfigurationProperty;
	
	@Test
	void managePackage() {

		Customer customerDetails = Customer.builder()
                .organizationName("Test")
                .contactNumber("Test")
                .city("DE")
                .email("test@test.com")
                .build();

		SelectedTools selectedTools = SelectedTools.builder().tool(ToolType.SDE_WITH_EDC_TRACTUS).label("SDE").build();
		Map<String, String> mockInputMap = new HashMap<>();
		mockInputMap.put("dnsName", "test");
		mockInputMap.put("dnsNameURLProtocol", "https");
		Map<String, String> resultMap = sdeManager.managePackage(customerDetails, AppActions.CREATE, selectedTools,
				mockInputMap, null);
		assertEquals(39, resultMap.size());
		assertEquals("test", mockInputMap.get("dnsName"));
	}
}