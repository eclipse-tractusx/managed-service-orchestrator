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

package com.autosetup.manager;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.autosetup.model.Customer;
import com.autosetup.proxy.portal.PortalIntegrationProxy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortalIntegrationManager {

	private final PortalIntegrationProxy portalIntegrationProxy;

	public Map<String, String> getDigitalandKeyCloackDetails(Customer customerDetails,
			Map<String, String> inputData) {

		// String dftFrontEndUrl = inputData.get("dftFrontEndUrl");

		// String digitaltwinandkeycloakdetails =
		// portalIntegrationProxy.getDigitaltwinandkeycloakdetails(dftfrontendUrl);

//		inputData.put("digital-twins.hostname", "https://semantics.dev.demo.catena-x.net");
//		inputData.put("digital-twins.authentication.url",
//				"https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/token");
//		inputData.put("digital-twins.authentication.clientId", "sa-cl6-cx-17");
//		inputData.put("digital-twins.authentication.clientSecret", "Fc82eBzxmqSGkmRykBwqRdoYiJ3xVFyy");

//		inputData.put("dftkeycloakurl", dftUpdateRequest.getKeycloakUrl());
//		inputData.put("dftcloakrealm", dftUpdateRequest.getKeycloakRealm());
//		inputData.put("dftbackendkeycloakclientid", dftUpdateRequest.getKeycloakBackendClientId());
//		inputData.put("dftfrontendkeycloakclientid", dftUpdateRequest.getKeycloakFrontendClientId());


		return inputData;
	}

}
