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

package org.eclipse.tractusx.autosetup.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.tractusx.autosetup.entity.AppDetails;
import org.eclipse.tractusx.autosetup.model.AppDetailsRequest;
import org.eclipse.tractusx.autosetup.service.AppDetailsService;
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
class AppDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppDetailsService appDetailsService;

    @Test
    void createOrUpdateAppInfo() throws Exception {
        String request = "{\n" +
                "  \"appName\": \"EDC_DATAPLANE\",\n" +
                "  \"contextCluster\": \"default\",\n" +
                "  \"contextNamespace\": \"kubeapps\",\n" +
                "  \"packageIdentifier\": \"edcrepo/edc-dataplane\",\n" +
                "  \"pluginName\": \"helm.packages\",\n" +
                "  \"pluginVersion\": \"v1alpha1\",\n" +
                "  \"packageVersion\": \"0.1.1\",\n" +
                "  \"expectedInputData\": \"edc.hostname=${dnsName}\\n\\t\\nedc.vault.hashicorp.url=${vaulturl}\\n\\nedc.vault.hashicorp.token=${vaulttoken}\\n\\nedc.vault.hashicorp.timeout.seconds=${vaulttimeout}\\n\\nedc.dataplane.token.validation.endpoint=${controlPlaneValidationEndpoint}\",\n" +
                "  \"outputData\": null,\n" +
                "  \"requiredYamlConfiguration\": \"{\\\"ingresses\\\":[{\\\"enabled\\\": true, \\\"hostname\\\": \\\"${dnsName}\\\", \\\"annotations\\\": {}, \\\"className\\\": \\\"nginx\\\", \\\"endpoints\\\":[\\\"public\\\"], \\\"tls\\\":{\\\"enabled\\\": true, \\\"secretName\\\":\\\"edcdataplane\\\"},\\\"certManager\\\":{\\\"clusterIssuer\\\":\\\"letsencrypt-staging\\\"}}], \\\"configuration\\\": {\\\"properties\\\": \\\"${yamlValues}\\\"}}\",\n" +
                "  \"yamlValueFieldType\": \"PROPERTY\"\n" +
                "}";

        String response = "{\n" +
                "    \"appName\": \"EDC_DATAPLANE\",\n" +
                "    \"contextCluster\": \"default\",\n" +
                "    \"contextNamespace\": \"kubeapps\",\n" +
                "    \"packageIdentifier\": \"edcrepo/edc-dataplane\",\n" +
                "    \"pluginName\": \"helm.packages\",\n" +
                "    \"pluginVersion\": \"v1alpha1\",\n" +
                "    \"packageVersion\": \"0.1.1\",\n" +
                "    \"expectedInputData\": \"edc.hostname=${dnsName}\\n\\t\\nedc.vault.hashicorp.url=${vaulturl}\\n\\nedc.vault.hashicorp.token=${vaulttoken}\\n\\nedc.vault.hashicorp.timeout.seconds=${vaulttimeout}\\n\\nedc.dataplane.token.validation.endpoint=${controlPlaneValidationEndpoint}\",\n" +
                "    \"outputData\": null,\n" +
                "    \"requiredYamlConfiguration\": \"{\\\"ingresses\\\":[{\\\"enabled\\\": true, \\\"hostname\\\": \\\"${dnsName}\\\", \\\"annotations\\\": {}, \\\"className\\\": \\\"nginx\\\", \\\"endpoints\\\":[\\\"public\\\"], \\\"tls\\\":{\\\"enabled\\\": true, \\\"secretName\\\":\\\"edcdataplane\\\"},\\\"certManager\\\":{\\\"clusterIssuer\\\":\\\"letsencrypt-staging\\\"}}], \\\"configuration\\\": {\\\"properties\\\": \\\"${yamlValues}\\\"}}\",\n" +
                "    \"yamlValueFieldType\": \"PROPERTY\"\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        AppDetails mockAppDetails = null;

        mockAppDetails =mapper.readValue(response, AppDetails.class);
        Mockito.when(
                appDetailsService.createOrUpdateAppInfo(
                                Mockito.any(AppDetailsRequest.class)))
                .thenReturn(mockAppDetails);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/internal/app-details")
                .accept(MediaType.APPLICATION_JSON).content(request)
                //.header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzNHRndXFfV3N0V3FlYnpkWXRYTkpnVF93SDJXd3lXRC1qS1lOQUNWQkRvIn0.eyJleHAiOjE2NjQ3ODE3NzUsImlhdCI6MTY2NDc4MTQ3NSwianRpIjoiZDAxZDlmOGUtMDJlOC00YTBlLTgyNTItYmQxNThmMDhjZTM1IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay5jeC5kaWgtY2xvdWQuY29tL3JlYWxtcy9hdXRvc2V0dXAiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNTFjYzA5YWItODE5ZS00MjkyLTljNGQtNWQyOWU3NWQ0ODUyIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYXV0b3NldHVwLXNlcnZpY2UiLCJzZXNzaW9uX3N0YXRlIjoiYTI5ZDY0ZjktY2Q2MS00OTY3LWI0ZjUtY2RjYzE5YzM1OTUyIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLWF1dG9zZXR1cCIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhdXRvc2V0dXAtc2VydmljZSI6eyJyb2xlcyI6WyJwa2ctaW5pdGlhdG9yIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiJhMjlkNjRmOS1jZDYxLTQ5NjctYjRmNS1jZGNjMTljMzU5NTIiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJjeCB0ZXN0aW5nIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiY3gtdGVzdGluZyIsImdpdmVuX25hbWUiOiJjeCIsImZhbWlseV9uYW1lIjoidGVzdGluZyIsImVtYWlsIjoiY3gtdGVzdGluZ0BhdXRvc2V0dXAuY29tIn0.hBOPCc7tYibljiRWP4zXcOFPmN1uLnWUTzlYogpIgzWHcJRLvVY9uHiWT5DGLJoRE_Jk2fki4uBfb94vPLGul9DxaxXEOx22g4kadPe9TaorUGA5iYruV3Z25Cx2UMaq1B8L7snxu3xuBAwed9J_ulspL0dWs4Pr4GVVozaZ4Ut6Yo5GTVVHpgpUvV95fSDbBBwIzPL42SRnbX-tF-bztaesUtTJ4zNo4W1IpIg00x73THfRkQXwKfvhWI51THfpVdAZASV6m2smKr9h8B6Qqjqp_wqcCnPNgzwpdy4yrElu2BiVHyT7H-B9H4GfYPDDlpa7yVzXxD5On66dx8auhw")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse apiResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), apiResponse.getStatus());
    }
}