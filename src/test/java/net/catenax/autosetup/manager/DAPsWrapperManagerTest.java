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

package net.catenax.autosetup.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.Resources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import net.catenax.autosetup.constant.ToolType;
import net.catenax.autosetup.daps.proxy.DAPsWrapperProxy;
import net.catenax.autosetup.model.Customer;
import net.catenax.autosetup.model.SelectedTools;
import net.catenax.autosetup.utility.Certutil;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class DAPsWrapperManagerTest {

    @Mock
    private DAPsWrapperProxy dapsWrapperProxy;

    @Mock
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @InjectMocks
    private DAPsWrapperManager daPsWrapperManager;

    @Test
    void createClient() throws IOException {
        Map<String, String> mockInputMap = new HashMap<>();
        mockInputMap.put("targetCluster","test");

        try (var pemStream = Resources.getInputStream("cx-test.crt")) {
            var pem = new String(pemStream.readAllBytes());
            var cert = Certutil.loadCertificate(pem);

        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.DFT)
                .label("DFT")
                .build();

        Customer customer = Customer.builder()
                .organizationName("Test")
                .contactNumber("Test")
                .city("DE")
                .build();
        mockInputMap.put("selfsigncertificate", Certutil.getAsString(cert));
        mockInputMap = daPsWrapperManager.createClient(customer, selectedTools, mockInputMap, null);
        assertEquals(5, mockInputMap.size());
        assertEquals("test", mockInputMap.get("targetCluster"));
    } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }}