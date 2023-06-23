/********************************************************************************
 * Copyright (c)  2023 T-Systems International GmbH
 * Copyright (c)  2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.autosetup.apiproxy;

import java.net.URI;
import java.util.Map;

import org.eclipse.tractusx.autosetup.portal.model.ServiceInstanceResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.fasterxml.jackson.databind.node.ObjectNode;

import feign.Headers;

@FeignClient(name = "EDCApiProxy", url = "placeholder")
public interface EDCApiProxy {

	@PostMapping(value ="/v2/assets/request", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String getAssets(URI url, @RequestHeader Map<String, String> header);

	@PostMapping("/v2/assets")
	@Headers("Content-Type: application/json")
	public ServiceInstanceResultResponse createAsset(URI url, @RequestHeader Map<String, String> header,
			@RequestBody ObjectNode requestBody);

	@PostMapping("/v2/policydefinitions")
	@Headers("Content-Type: application/json")
	public ServiceInstanceResultResponse createPolicy(URI url, @RequestHeader Map<String, String> header,
			@RequestBody ObjectNode requestBody);

	@PostMapping("/v2/contractdefinitions")
	@Headers("Content-Type: application/json")
	public ServiceInstanceResultResponse createContractDefination(URI url, @RequestHeader Map<String, String> header,
			@RequestBody ObjectNode requestBody);

}
