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

package org.eclipse.tractusx.autosetup.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.tractusx.autosetup.manager.AutoSetupTriggerManager;
import org.eclipse.tractusx.autosetup.model.AutoSetupRequest;
import org.eclipse.tractusx.autosetup.model.AutoSetupResponse;
import org.eclipse.tractusx.autosetup.model.DFTUpdateRequest;
import org.eclipse.tractusx.autosetup.service.AutoSetupOrchitestratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class AutoSetupHandlerControllerTest {

    @MockBean
    private AutoSetupOrchitestratorService appHandlerService;

    @MockBean
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void updateDftPackage() throws Exception {
        String request = "{\n" +
                "    \"keycloakUrl\": \"https://keycloak.cx.dih-cloud.com\",\n" +
                "    \"keycloakRealm\": \"orchestrator\",\n" +
                "    \"keycloakFrontendClientId\": \"orchestratorservice\",\n" +
                "    \"keycloakBackendClientId\": \"orchestratorservice\",\n" +
                "    \"digitalTwinUrl\": \"https://semantics.dev.demo.catena-x.net/registry\",\n" +
                "    \"digitalTwinAuthUrl\": \"https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/token\",\n" +
                "    \"digitalTwinClientId\": \"sa-cl6-cx-17\",\n" +
                "    \"digitalTwinClientSecret\": \"T8yxHEoPdluIQkPwJi3KSGf3mcSTWoij\"\n" +
                "}";
        String response = "1ca680dc-8947-4afa-9621-2a72a31f9bb9";
        Mockito.when(appHandlerService.updateDftPackage(Mockito.anyString(),Mockito.any(DFTUpdateRequest.class))).thenReturn(response);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/internal/update-package/1ca680dc-8947-4afa-9621-2a72a31f9bb9")
                .accept(MediaType.APPLICATION_JSON).content(request)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse apiResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());
    }

    @Test
    void createPackage() throws Exception {
        String request = "{\n" +
                "    \"customer\": {\n" +
                "        \"organizationName\": \"Verul\",\n" +
                "        \"country\": \"IN\",\n" +
                "        \"state\": \"GN\",\n" +
                "        \"city\": \"BL\",\n" +
                "        \"email\": \"sachin.argade@t-systems.com\"\n" +
                "    },\n" +
                "    \"properties\": {\n" +
                "        \"bpnNumber\": \"BPN12345611\",\n" +
                "        \"role\": \"recycler\",\n" +
                "        \"subscriptionId\": \"DAS-D234\",\n" +
                "        \"serviceId\": \"T-SYSTEM-DFT-EDC\"\n" +
                "    }\n" +
                "}";
        String response = "1ca680dc-8947-4afa-9621-2a72a31f9bb9";
        Mockito.when(appHandlerService.createPackage(Mockito.any(AutoSetupRequest.class))).thenReturn(response);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/autosetup")
                .accept(MediaType.APPLICATION_JSON).content(request)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse apiResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());
    }

    @Test
    void updatePackage() throws Exception {
        String request = "{\n" +
                "    \"customer\": {\n" +
                "        \"organizationName\": \"Verul\",\n" +
                "        \"country\": \"IN\",\n" +
                "        \"state\": \"GN\",\n" +
                "        \"city\": \"BL\",\n" +
                "        \"email\": \"sachin.argade@t-systems.com\"\n" +
                "    },\n" +
                "    \"properties\": {\n" +
                "        \"bpnNumber\": \"BPN12345611\",\n" +
                "        \"role\": \"recycler\",\n" +
                "        \"subscriptionId\": \"DAS-D234\",\n" +
                "        \"serviceId\": \"T-SYSTEM-DFT-EDC\"\n" +
                "    }\n" +
                "}";
        String response = "1ca680dc-8947-4afa-9621-2a72a31f9bb9";
        Mockito.when(appHandlerService.updatePackage(Mockito.any(AutoSetupRequest.class), Mockito.anyString())).thenReturn(response);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/autosetup/1ca680dc-8947-4afa-9621-2a72a31f9bb9")
                .accept(MediaType.APPLICATION_JSON).content(request)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse apiResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());
    }

    @Test
    void deletePackage() throws Exception {
        String request = "{\n" +
                "    \"customer\": {\n" +
                "        \"organizationName\": \"Verul\",\n" +
                "        \"country\": \"IN\",\n" +
                "        \"state\": \"GN\",\n" +
                "        \"city\": \"BL\",\n" +
                "        \"email\": \"sachin.argade@t-systems.com\"\n" +
                "    },\n" +
                "    \"properties\": {\n" +
                "        \"bpnNumber\": \"BPN12345611\",\n" +
                "        \"role\": \"recycler\",\n" +
                "        \"subscriptionId\": \"DAS-D234\",\n" +
                "        \"serviceId\": \"T-SYSTEM-DFT-EDC\"\n" +
                "    }\n" +
                "}";
        String response = "1ca680dc-8947-4afa-9621-2a72a31f9bb9";
        Mockito.when(appHandlerService.deletePackage(Mockito.anyString())).thenReturn(response);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/autosetup/1ca680dc-8947-4afa-9621-2a72a31f9bb9")
                .accept(MediaType.APPLICATION_JSON).content(request)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse apiResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());
    }

    @Test
    void getCheckDetails() throws Exception {

        String response = "{\n" +
                "    \"executionId\": \"1ca680dc-8947-4afa-9621-2a72a31f9bb9\",\n" +
                "    \"executionType\": \"DELETE\",\n" +
                "    \"request\": {\n" +
                "        \"customer\": {\n" +
                "            \"organizationName\": \"Verul\",\n" +
                "            \"email\": \"sachin.argade@t-systems.com\",\n" +
                "            \"country\": \"IN\",\n" +
                "            \"state\": \"GN\",\n" +
                "            \"city\": \"BL\"\n" +
                "        },\n" +
                "        \"properties\": {\n" +
                "            \"bpnNumber\": \"BPN12345611\",\n" +
                "            \"subscriptionId\": \"DAS-D234\",\n" +
                "            \"serviceId\": \"T-SYSTEM-DFT-EDC\",\n" +
                "            \"role\": \"recycler\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"processResult\": [],\n" +
                "    \"status\": \"SUCCESS\",\n" +
                "    \"createdTimestamp\": \"2022-10-04T08:42:30.139346106\",\n" +
                "    \"modifiedTimestamp\": \"2022-10-07T10:46:34.356650371\"\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        AutoSetupResponse autoSetupResponse = null;

        autoSetupResponse =mapper.readValue(response, AutoSetupResponse.class);

        Mockito.when(autoSetupTriggerManager.getCheckDetails(Mockito.anyString())).thenReturn(autoSetupResponse);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/autosetup/1ca680dc-8947-4afa-9621-2a72a31f9bb9")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse apiResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());
    }
}