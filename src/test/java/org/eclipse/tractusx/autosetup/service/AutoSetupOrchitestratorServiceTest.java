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

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.tractusx.autosetup.kubeapps.proxy.KubeAppManageProxy;
import org.eclipse.tractusx.autosetup.mapper.AutoSetupRequestMapper;
import org.eclipse.tractusx.autosetup.model.AutoSetupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class AutoSetupOrchitestratorServiceTest {

    //@Spy
    @Autowired
    private AutoSetupOrchitestratorService autoSetupOrchitestratorService;


    @MockBean
    private AutoSetupRequestMapper customerDetailsMapper;

    @MockBean
    private KubeAppManageProxy kubeAppManageProxy;

    @Test
    void createPackage() {

        String json = "{\n" +
                "    \"customer\": {\n" +
                "        \"organizationName\": \"Verul1\",\n" +
                "        \"country\": \"IN\",\n" +
                "        \"state\": \"GN\",\n" +
                "        \"city\": \"BL\",\n" +
                "        \"email\": \"sachin.argade@t-systems.com\"\n" +
                "    },\n" +
                "    \"properties\": {\n" +
                "        \"bpnNumber\": \"BPN12345611\",\n" +
                "        \"role\": \"recycler\",\n" +
                "        \"subscriptionId\": \"DAS-D234\",\n" +
                "        \"serviceId\": \"DFT-WITH-EDC\"\n" +
                "    }\n" +
                "}";
        try {
            AutoSetupRequest autoSetupRequest = new ObjectMapper().readValue(json,AutoSetupRequest.class);
            Mockito.when(customerDetailsMapper.fromCustomer(Mockito.any(AutoSetupRequest.class))).thenReturn(json);
            Mockito.when(autoSetupOrchitestratorService.checkNamespaceisExist(Mockito.anyString())).thenReturn(true);
            String uuid = autoSetupOrchitestratorService.createPackage(autoSetupRequest);
            assertThat(uuid).isNotEmpty();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}