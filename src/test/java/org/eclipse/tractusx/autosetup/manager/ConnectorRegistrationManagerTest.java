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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Resources;
import org.eclipse.tractusx.autosetup.constant.ToolType;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.portal.proxy.PortalIntegrationProxy;
import org.eclipse.tractusx.autosetup.utility.Certutil;
//import org.eclipse.tractusx.autosetup.utility.KeyCloakTokenProxyUtitlity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class ConnectorRegistrationManagerTest {

	@Mock
	private AutoSetupTriggerManager autoSetupTriggerManager;

	@Mock
	private PortalIntegrationManager portalIntegrationManager;

	@Mock
	private PortalIntegrationProxy portalIntegrationProxy;

	@InjectMocks
	private ConnectorRegistrationManager connectorRegistrationManager;
	
	@InjectMocks
	private AutoSetupTriggerEntry triger;
	
//	@Mock
//	private KeyCloakTokenProxyUtitlity keyCloakTokenProxyUtitlity;

	@Test
    void createClient() throws IOException {
        Map<String, String> mockInputMap = new HashMap<>();
        mockInputMap.put("targetCluster","test");

        try (var pemStream = Resources.getInputStream("cx-test.crt")) {
            var pem = new String(pemStream.readAllBytes());
            var cert = Certutil.loadCertificate(pem);

        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.SDE_WITH_EDC_TRACTUS)
                .label("SDE")
                .build();

        Customer customer = Customer.builder()
                .organizationName("Test")
                .contactNumber("Test")
                .city("DE")
                .build();

        when(portalIntegrationProxy.manageConnector((URI)any(), any(), any())).thenReturn("CONNECTOR123");
        
        mockInputMap.put("selfsigncertificate", Certutil.getAsString(cert));
        mockInputMap = connectorRegistrationManager.registerConnector(customer, selectedTools, mockInputMap, triger);
        assertEquals(3, mockInputMap.size());
        assertEquals("ACTIVE", mockInputMap.get("connectorstatus"));
    } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }
}