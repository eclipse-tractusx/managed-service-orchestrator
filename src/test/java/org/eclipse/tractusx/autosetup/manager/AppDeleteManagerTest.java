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

import org.eclipse.tractusx.autosetup.constant.AppNameConstant;
import org.eclipse.tractusx.autosetup.constant.ToolType;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
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
class AppDeleteManagerTest {

    @Mock
    private KubeAppsPackageManagement appManagement;

    @Mock
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @InjectMocks
    private AppDeleteManager appDeleteManager;

    @Test
    void deletePackage() {
        //Handle exception
        Map<String, String> mockInputMap = new HashMap<>();
        mockInputMap.put("targetCluster","test");

        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.EDC_TRACTUS)
                .label("EDC_TRACTUS")
                .build();

        mockInputMap = appDeleteManager.deletePackage(AppNameConstant.EDC_CONNECTOR, selectedTools, mockInputMap, null);

        assertEquals(1, mockInputMap.size());
        assertEquals("test", mockInputMap.get("targetCluster"));
    }
}