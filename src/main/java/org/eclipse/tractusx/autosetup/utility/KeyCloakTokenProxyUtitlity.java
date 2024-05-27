/********************************************************************************
 * Copyright (c) 2024 T-Systems International GmbH
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.autosetup.utility;

import java.net.URI;

import org.eclipse.tractusx.autosetup.portal.proxy.PortalIntegrationProxy;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class KeyCloakTokenProxyUtitlity {
	
	private final PortalIntegrationProxy portalIntegrationProxy;
	
	@SneakyThrows
	public String getKeycloakToken(String clientId, String clientSecret, URI tokenURI) {

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "client_credentials");
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		var resultBody = portalIntegrationProxy.readAuthToken(tokenURI, body);

		if (resultBody != null) {
			return resultBody.getAccessToken();
		}
		return null;

	}

}
