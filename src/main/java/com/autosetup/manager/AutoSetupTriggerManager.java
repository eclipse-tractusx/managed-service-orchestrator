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

import static com.autosetup.constant.TriggerStatusEnum.FAILED;
import static com.autosetup.constant.TriggerStatusEnum.INPROGRESS;
import static com.autosetup.constant.TriggerStatusEnum.SUCCESS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.autosetup.constant.AppActions;
import com.autosetup.entity.AutoSetupTriggerDetails;
import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.exception.NoDataFoundException;
import com.autosetup.kubeapp.mapper.AutoSetupRequestMapper;
import com.autosetup.kubeapp.mapper.AutoSetupTriggerMapper;
import com.autosetup.model.AutoSetupRequest;
import com.autosetup.model.AutoSetupResponse;
import com.autosetup.model.AutoSetupTriggerResponse;
import com.autosetup.repository.AutoSetupTriggerEntryRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class AutoSetupTriggerManager {

	private final AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;
	private final AutoSetupRequestMapper customerDetailsMapper;
	private final AutoSetupTriggerMapper autoSetupTriggerMapper;

	public AutoSetupTriggerEntry createTrigger(AutoSetupRequest autoSetupRequest, AppActions action, String triggerId,
			String tenantNamespace) {
		LocalDateTime now = LocalDateTime.now();
		AutoSetupTriggerEntry autoSetupTriggerEntry = AutoSetupTriggerEntry.builder()
				.organizationName(autoSetupRequest.getCustomer().getOrganizationName())
				.autosetupRequest(customerDetailsMapper.fromCustomer(autoSetupRequest)).triggerId(triggerId)
				.triggerType(action.name()).createdTimestamp(now.toString()).modifiedTimestamp(now.toString())
				.status(INPROGRESS.name()).autosetupTenantName(tenantNamespace).build();

		return autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerEntry saveTriggerUpdate(AutoSetupTriggerEntry autoSetupTriggerEntry) {
		LocalDateTime now = LocalDateTime.now();
		autoSetupTriggerEntry.setModifiedTimestamp(now.toString());
		return autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerEntry updateTriggerAutoSetupRequest(AutoSetupRequest autoSetupRequest,
			AutoSetupTriggerEntry autoSetupTriggerEntry, AppActions action) {

		autoSetupTriggerEntry.setTriggerType(action.name());
		autoSetupTriggerEntry.setOrganizationName(autoSetupRequest.getCustomer().getOrganizationName());
		autoSetupTriggerEntry.setAutosetupRequest(customerDetailsMapper.fromCustomer(autoSetupRequest));
		autoSetupTriggerEntry.setStatus(INPROGRESS.name());
		LocalDateTime now = LocalDateTime.now();
		autoSetupTriggerEntry.setModifiedTimestamp(now.toString());

		return autoSetupTriggerEntryRepository.save(autoSetupTriggerEntry);
	}

	@SneakyThrows
	public AutoSetupTriggerDetails saveTriggerDetails(AutoSetupTriggerDetails autoSetupTriggerDetails,
			AutoSetupTriggerEntry trigger) {
		autoSetupTriggerDetails.setCreatedDate(LocalDateTime.now());
		autoSetupTriggerDetails.setAction(trigger.getTriggerType());
		trigger.addTriggerDetails(autoSetupTriggerDetails);
		autoSetupTriggerEntryRepository.save(trigger);
		return autoSetupTriggerDetails;
	}

	public List<AutoSetupTriggerResponse> getAllTriggers() {
		return Optional.of(autoSetupTriggerEntryRepository.findAll()).orElseGet(ArrayList::new).stream()
				.map((obj) -> autoSetupTriggerMapper.fromEntitytoCustom(obj)).toList();
	}

	public AutoSetupTriggerResponse getTriggerDetails(String triggerId) {
		return autoSetupTriggerEntryRepository.findById(triggerId)
				.map((obj) -> autoSetupTriggerMapper.fromEntitytoCustom(obj))
				.orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));

	}

	public AutoSetupResponse getCheckDetails(String triggerId) {

		return Optional.ofNullable(autoSetupTriggerEntryRepository.findAllByTriggerId(triggerId)).map(obj -> {
			AutoSetupResponse newobj = autoSetupTriggerMapper.fromEntitytoAutoSetupCustom(obj);

			newobj.setRemark(null);
			if (FAILED.name().equals(obj.getStatus())) {
				newobj.setRemark("Please connect with technical team for more advice");
			}

			if (!SUCCESS.name().equals(obj.getStatus())) {
				newobj.setProcessResult(List.of());
			}
			return newobj;

		}).orElseThrow(() -> new NoDataFoundException("No data found for " + triggerId));

	}

	public AutoSetupTriggerEntry isAutoSetupAvailableforOrgnizationName(String organizationName) {
		return autoSetupTriggerEntryRepository.findTop1ByOrganizationName(organizationName);
	}

}
