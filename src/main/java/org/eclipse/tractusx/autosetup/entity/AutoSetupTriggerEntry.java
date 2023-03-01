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

package org.eclipse.tractusx.autosetup.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "auto_setup_trigger_tbl")
@JsonInclude(Include.NON_NULL)
public class AutoSetupTriggerEntry {

	@Id
	@Column(name = "trigger_id")
	private String triggerId;

	@Column(name = "trigger_type")
	private String triggerType;

	@Column(name = "organization_name")
	private String organizationName;
	
	@Column(name = "subscription_id")
	private String subscriptionId;
	
	@Column(name = "service_id")
	private String serviceId;

	@JsonIgnore
	@Column(name = "autosetup_tenant_name")
	private String autosetupTenantName;

	@OneToMany(targetEntity = AutoSetupTriggerDetails.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "trigger_id", referencedColumnName = "trigger_id")
	private List<AutoSetupTriggerDetails> autosetupTriggerDetails;

	@Column(name = "autosetup_request", columnDefinition = "TEXT")
	private String autosetupRequest;

	@Column(name = "autosetup_result", columnDefinition = "TEXT")
	private String autosetupResult;

	@Column(name = "created_timestamp")
	private String createdTimestamp;

	@Column(name = "modified_timestamp")
	private String modifiedTimestamp;

	@Column(name = "status")
	private String status;

	@Column(name = "remark", columnDefinition = "TEXT")
	private String remark;

	public void addTriggerDetails(AutoSetupTriggerDetails autoSetupTriggerDetails) {
		if (autosetupTriggerDetails == null)
			autosetupTriggerDetails = new ArrayList<>();
		autosetupTriggerDetails.add(autoSetupTriggerDetails);
	}
}
