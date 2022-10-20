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
package org.eclipse.tractusx.autosetup.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.manager.InputConfigurationManager;
import org.eclipse.tractusx.autosetup.model.AutoSetupRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
class InputConfigurationManagerTest {
    @Autowired
    private InputConfigurationManager inputConfigurationManager;

    @Test
    void prepareInputConfiguration() {

        String json = "{\n" +
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
        String uuID = UUID.randomUUID().toString();
        try {
            AutoSetupRequest autoSetupRequest = new ObjectMapper().readValue(json,AutoSetupRequest.class);
            Map<String, String>resultMap =  inputConfigurationManager.prepareInputConfiguration(autoSetupRequest, uuID);
            assertEquals(8, resultMap.size());
            assertEquals("BPN12345611", resultMap.get("bpnNumber"));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void prepareInputFromDBObject() {

        AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder().autosetupTenantName("test").build();
        Map<String, String>resultMap =  inputConfigurationManager.prepareInputFromDBObject(autoSetupTriggerEntry);
        assertEquals(4, resultMap.size());
    }
}