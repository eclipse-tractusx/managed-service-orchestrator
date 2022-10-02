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

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import net.catenax.autosetup.model.AutoSetupRequest;

@Mapper(componentModel = "spring")
public interface AutoSetupRequestMapper {

	@SneakyThrows
	public default String fromCustomer(AutoSetupRequest request) {
		ObjectMapper mapper = new ObjectMapper();
		if (request == null)
			return "";
		return mapper.writeValueAsString(request);
	}

	@SneakyThrows
	public default AutoSetupRequest fromStr(String requetsstr) {
		ObjectMapper mapper = new ObjectMapper();

		if (StringUtils.isBlank(requetsstr))
			return null;
		return mapper.readValue(requetsstr, AutoSetupRequest.class);
	}

}
