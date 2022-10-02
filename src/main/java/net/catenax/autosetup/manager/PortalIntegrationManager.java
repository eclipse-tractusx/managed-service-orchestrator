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

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.catenax.autosetup.portal.model.ServiceInstanceResultRequest;
import net.catenax.autosetup.portal.proxy.PortalIntegrationProxy;

@Service
@RequiredArgsConstructor
public class PortalIntegrationManager {

	private final PortalIntegrationProxy portalIntegrationProxy;

	@SneakyThrows
	public Map<String, String> postServiceInstanceResultAndGetTenantSpecs(Map<String, String> inputData) {

		String dftFrontendURL = inputData.get("dftFrontEndUrl");
		String subscriptionId = inputData.get("subscriptionId");
		
		ServiceInstanceResultRequest serviceInstanceResultRequest = ServiceInstanceResultRequest
				.builder()
				.requestId(subscriptionId)
				.appUrl(dftFrontendURL)
				.build();
		portalIntegrationProxy.postServiceInstanceResultAndGetTenantSpecs(serviceInstanceResultRequest);
		return inputData;
	}

}
