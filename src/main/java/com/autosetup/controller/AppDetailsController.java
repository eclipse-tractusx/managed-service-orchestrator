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

package com.autosetup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autosetup.entity.AppDetails;
import com.autosetup.model.AppDetailsRequest;
import com.autosetup.service.AppDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class AppDetailsController {

	@Autowired
	private AppDetailsService appDetailsService;

	/// internal access
	@Operation(summary = "This will create/update app in kubeapps", description = "This will create/update app in kubeapps")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AppDetails.class))) })
	@PostMapping("/internal/app-details")
	public AppDetails createOrUpdateAppInfo(@RequestBody AppDetailsRequest appDetailsRequest) {
		return appDetailsService.createOrUpdateAppInfo(appDetailsRequest);
	}

	/// internal access
	@Operation(summary = "This will fetch specific app details in kubeapps", description = "This will fetch specific app details in kubeapps")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AppDetails.class))) })
	@GetMapping("/internal/app-details/{appName}")
	public AppDetails getAppInfo(@PathVariable("appName") String appName) {
		return appDetailsService.getAppDetails(appName);
	}

	/// internal access
	@Operation(summary = "This will fetch all app details in kubeapps", description = "This will fetch all app details in kubeapps")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppDetails.class)))) })
	@GetMapping("/internal/app-details")
	public List<AppDetails> getAllAppInfo() {
		return appDetailsService.getAppDetails();
	}

}
