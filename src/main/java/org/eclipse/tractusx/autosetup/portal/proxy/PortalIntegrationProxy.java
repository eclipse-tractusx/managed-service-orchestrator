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

package org.eclipse.tractusx.autosetup.portal.proxy;

import java.net.URI;
import java.util.Map;

import org.eclipse.tractusx.autosetup.model.KeycloakTokenResponse;
import org.eclipse.tractusx.autosetup.portal.model.ServiceInstanceResultRequest;
import org.eclipse.tractusx.autosetup.portal.model.ServiceInstanceResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "PortalIntegrationProxy", url = "placeholder")
public interface PortalIntegrationProxy {

	@PostMapping
	KeycloakTokenResponse readAuthToken(URI url, @RequestBody MultiValueMap<String, Object> body);

	@PostMapping("/api/Apps/autoSetup")
	public ServiceInstanceResultResponse postServiceInstanceResultAndGetTenantSpecs(URI url,
			@RequestHeader Map<String, String> header,
			@RequestBody ServiceInstanceResultRequest serviceInstanceResultRequest);

	@PostMapping("/api/administration/connectors/managed-daps")
	public String manageConnector(URI url, @RequestHeader Map<String, String> header,
			@RequestBody MultiValueMap<String, Object> body);

	@DeleteMapping("/api/administration/connectors/{connectorId}")
	public void deleteConnector(URI url, @RequestHeader Map<String, String> header, @PathVariable String connectorId);

}
