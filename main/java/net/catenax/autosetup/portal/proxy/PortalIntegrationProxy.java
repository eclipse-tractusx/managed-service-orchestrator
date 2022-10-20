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

package net.catenax.autosetup.portal.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.catenax.autosetup.model.KeycloakTokenResponse;
import net.catenax.autosetup.portal.model.ServiceInstanceResultRequest;
import net.catenax.autosetup.portal.model.ServiceInstanceResultResponse;

@FeignClient(name = "PortalIntegrationProxy", url = "${portal.url}", configuration = PortalIntegrationConfiguration.class)
public interface PortalIntegrationProxy {

	@PostMapping(path = "/token")
	KeycloakTokenResponse readAuthToken(@RequestParam("grant_type") String grant_type,
			@RequestParam("client_id") String client_id, @RequestParam("client_secret") String client_secret,
			@RequestParam("scope") String scope);

	@PostMapping(path = "/portal")
	public ServiceInstanceResultResponse postServiceInstanceResultAndGetTenantSpecs(
			@RequestBody ServiceInstanceResultRequest serviceInstanceResultRequest);

}
