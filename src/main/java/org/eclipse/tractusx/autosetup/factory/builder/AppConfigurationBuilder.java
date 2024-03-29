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

package org.eclipse.tractusx.autosetup.factory.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.eclipse.tractusx.autosetup.entity.AppDetails;
import org.springframework.stereotype.Service;

import lombok.SneakyThrows;

@Service
public class AppConfigurationBuilder {

	@SneakyThrows
	public String buildConfiguration(AppDetails appDetails, Map<String, String> inputProperties) {

		Map<String, Object> dyanamicYamlValues = new HashMap<>();
		
		// Initialize StringSubstitutor instance with value map
		StringSubstitutor stringSubstitutor1 = new StringSubstitutor(inputProperties);
		String sb=stringSubstitutor1.replace(appDetails.getExpectedInputData());
		dyanamicYamlValues.put("yamlValues", sb);
		dyanamicYamlValues.put("dnsName", inputProperties.get("dnsName"));
		dyanamicYamlValues.putAll(inputProperties);
		
		
		// Initialize StringSubstitutor instance with value map
		StringSubstitutor stringSubstitutor = new StringSubstitutor(dyanamicYamlValues);

		// replace value map to template string
		return stringSubstitutor.replace(appDetails.getRequiredYamlConfiguration());
	}

}
