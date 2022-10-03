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

package net.catenax.autosetup.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.catenax.autosetup.entity.AutoSetupTriggerEntry;
import net.catenax.autosetup.model.AutoSetupResponse;
import net.catenax.autosetup.model.AutoSetupTriggerResponse;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class AutoSetupTriggerMapper {

	@Autowired
	private AutoSetupRequestMapper autoSetupRequestMapper;

	public abstract AutoSetupTriggerResponse fromEntity(AutoSetupTriggerEntry autoSetupTriggerEntry);
	
	public abstract AutoSetupResponse fromEntityforCustomResponse(AutoSetupTriggerEntry autoSetupTriggerEntry);

	@SuppressWarnings("unchecked")
	@SneakyThrows
	public List<Map<String, String>> fromJsonStrToMap(String jsonStr) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (jsonStr != null && !jsonStr.isEmpty())
				return mapper.readValue(jsonStr, List.class);
			else
				return List.of();
		} catch (Exception e) {
			log.error("Error in read value of autosetup field result" + e.getMessage());
			return List.of();
		}

	}

	@SneakyThrows
	public String fromMaptoStr(List<Map<String, String>> listMap) {
		try {
			if (listMap != null && !listMap.isEmpty())
				return new ObjectMapper().writeValueAsString(listMap);
		} catch (Exception e) {
			log.error("Error in read value of autosetup field result" + e.getMessage());
		}
		return "{}";
	}

	public AutoSetupTriggerResponse fromEntitytoCustom(AutoSetupTriggerEntry autoSetupTriggerEntry) {

		AutoSetupTriggerResponse obj = fromEntity(autoSetupTriggerEntry);
		obj.setRequest(autoSetupRequestMapper.fromStr(obj.getAutosetupRequest()));
		obj.setProcessResult(fromJsonStrToMap(obj.getAutosetupResult()));
		return obj;

	}
	
	public AutoSetupResponse fromEntitytoAutoSetupCustom(AutoSetupTriggerEntry autoSetupTriggerEntry) {

		AutoSetupResponse obj = fromEntityforCustomResponse(autoSetupTriggerEntry);
		obj.setExecutionId(autoSetupTriggerEntry.getTriggerId());
		obj.setExecutionType(autoSetupTriggerEntry.getTriggerType());
		obj.setRequest(autoSetupRequestMapper.fromStr(autoSetupTriggerEntry.getAutosetupRequest()));
		obj.setProcessResult(fromJsonStrToMap(autoSetupTriggerEntry.getAutosetupResult()));
		return obj;

	}
	
	

}
