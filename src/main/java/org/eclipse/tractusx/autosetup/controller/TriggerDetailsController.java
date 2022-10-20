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

package org.eclipse.tractusx.autosetup.controller;

import java.util.List;

import org.eclipse.tractusx.autosetup.manager.AutoSetupTriggerManager;
import org.eclipse.tractusx.autosetup.model.AutoSetupTriggerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class TriggerDetailsController {

	@Autowired
	private AutoSetupTriggerManager autoSetupTriggerManager;

	/// internal access
	@Operation(summary = "Fetch all the orchestrator requests", description = "This will fetch all orchestrator request and their details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AutoSetupTriggerResponse.class)))) })
	@GetMapping("/internal/trigger")
	public List<AutoSetupTriggerResponse> getAllTriggers() {
		return autoSetupTriggerManager.getAllTriggers();
	}

	/// internal access
	@Operation(summary = "Fetch specific the orchestrator requests", description = "This will fetch specific orchestrator request and their details using trigger id in parth variable")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AutoSetupTriggerResponse.class))) })
	@GetMapping("/internal/trigger/{triggerId}")
	public AutoSetupTriggerResponse getTriggerDetails(@PathVariable("triggerId") String triggerId) {
		return autoSetupTriggerManager.getTriggerDetails(triggerId);
	}
	

}
