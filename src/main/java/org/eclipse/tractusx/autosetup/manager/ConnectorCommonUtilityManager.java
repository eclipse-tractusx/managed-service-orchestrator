/********************************************************************************
 * Copyright (c) 2023 T-Systems International GmbH
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.autosetup.manager;

import java.util.Map;

import org.eclipse.tractusx.autosetup.constant.DAPsConfigurationProperty;
import org.eclipse.tractusx.autosetup.utility.PasswordGenerator;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConnectorCommonUtilityManager {

	
	private final DAPsConfigurationProperty dAPsConfigurationProperty;

	public Map<String, String> prepareConnectorInput(String packageName, Map<String, String> inputData) {

		String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
		String dnsName = inputData.get("dnsName");
		String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

		String controlplaneurl = dnsNameURLProtocol + "://" + dnsName;

		inputData.put("dapsurl", dAPsConfigurationProperty.getUrl());
		inputData.put("dapsjsksurl", dAPsConfigurationProperty.getJskUrl());
		inputData.put("dapstokenurl", dAPsConfigurationProperty.getTokenUrl());

		inputData.put("dataPlanePublicUrl",
				dnsNameURLProtocol + "://" + packageName + "-edcdataplane-edc-dataplane:8185/api/public");

		String localControlplane = dnsNameURLProtocol + "://" + packageName
				+ "-edccontrolplane-edc-controlplane:8182/validation/token";

		inputData.put("controlPlaneValidationEndpoint", localControlplane);

		inputData.put("controlPlaneEndpoint", controlplaneurl);
		inputData.put("controlPlaneDataEndpoint", controlplaneurl + "/data");
		inputData.put("edcApiKey", "X-Api-Key");
		inputData.put("edcApiKeyValue", generateRandomPassword);
		inputData.put("controlPlaneIdsEndpoint", controlplaneurl + "/api/v1/ids/data");
		inputData.put("dataplaneendpoint", controlplaneurl);
		inputData.put("dataPlanePublicEndpoint", controlplaneurl + "/public");

		String dftAddress = dnsNameURLProtocol + "://" + dnsName + "/dftbackend/api";
		inputData.put("dftAddress", dftAddress);
		
		inputData.put("postgresPassword", "admin@123");
		inputData.put("username", "admin");
		inputData.put("appdbpass", "admin@123");

		String edcDb = "jdbc:postgresql://" + packageName + "-postgresdb-postgresql:5432/postgres";
		inputData.put("edcdatabaseurl", edcDb);

		return inputData;
	}
}
