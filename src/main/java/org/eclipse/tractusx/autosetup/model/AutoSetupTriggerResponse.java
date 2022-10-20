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

package org.eclipse.tractusx.autosetup.model;

import java.util.List;
import java.util.Map;

import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class AutoSetupTriggerResponse {
	
	private String triggerId;

	private String triggerType;

	private String organizationName;

	private List<AutoSetupTriggerDetails> autosetupTriggerDetails;

	private AutoSetupRequest request;
	
	private List<Map<String,String>> processResult;
	
	@JsonIgnore
	private String autosetupRequest;

	@JsonIgnore
	private String autosetupResult;

	private String createdTimestamp;

	private String modifiedTimestamp;

	private String status;

	private String remark;
	
}
