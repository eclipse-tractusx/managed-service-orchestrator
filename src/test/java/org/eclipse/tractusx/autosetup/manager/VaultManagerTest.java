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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.tractusx.autosetup.constant.ToolType;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.vault.proxy.VaultAppManageProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class VaultManagerTest {

    public static final String ENCRYPTIONKEYS = "encryptionkeys";
    public static final String CONTENT = "content";
    public static final String DAPS_CERT = "daps-cert";
    public static final String CERTIFICATE_PRIVATE_KEY = "certificate-private-key";

    @MockBean
    private VaultAppManageProxy vaultManagerProxy;

    @MockBean
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @Autowired
    private VaultManager vaultManager;

    @Value("${vault.url}")
    private String valutURL;

    @Value("${vault.token}")
    private String vaulttoken;

    @Value("${vault.timeout}")
    private String vaulttimeout;

    @Test
    void uploadKeyandValues() {
        AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
                .autosetupTenantName("Test")
                .build();
        Customer customer = Customer.builder()
                .organizationName("Test")
                .build();

        Map<String, String> mockInputMap = new HashMap<>();
        mockInputMap.put("targetCluster","test");
        mockInputMap.put("postgresPassword", "admin@123");
        mockInputMap.put("username", "admin");
        mockInputMap.put("password", "admin@123");
        mockInputMap.put("database", "postgres");

        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.SDE_WITH_EDC_TRACTUS)
                .label("SDE")
                .build();

        mockInputMap = vaultManager.uploadKeyandValues(customer, selectedTools,mockInputMap, autoSetupTriggerEntry);
        assertEquals(15, mockInputMap.size());
        assertEquals("test", mockInputMap.get("targetCluster"));
    }
}