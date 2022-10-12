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

import net.catenax.autosetup.constant.AppActions;
import net.catenax.autosetup.constant.ToolType;
import net.catenax.autosetup.model.SelectedTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DFTFrontendManagerTest {

    @InjectMocks
    private DFTFrontendManager dftFrontendManager;

    @Mock
    private KubeAppsPackageManagement appManagement;

    @Mock
    private AutoSetupTriggerManager autoSetupTriggerManager;

    @Test
    void managePackage() {

        SelectedTools selectedTools = SelectedTools.builder()
                .tool(ToolType.DFT)
                .label("dfttool")
                .build();
        Map<String, String> mockInputMap = new HashMap<>();

        mockInputMap.put("targetCluster","TestCluster");
        mockInputMap.put("targetNamespace","TestNamespace");
        Map<String, String> resultMap = dftFrontendManager.managePackage(null, AppActions.CREATE,selectedTools,mockInputMap,null);
        assertEquals(2, resultMap.size());
        assertEquals("TestCluster", mockInputMap.get("targetCluster"));
    }
}