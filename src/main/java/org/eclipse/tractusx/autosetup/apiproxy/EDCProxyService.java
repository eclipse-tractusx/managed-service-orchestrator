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

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.model.Customer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class EDCProxyService {

	private static final String CONTROL_PLANE_DATA_ENDPOINT = "controlPlaneDataEndpoint";

	private final EDCApiProxy eDCApiProxy;

	private Map<String, String> requestHeader(Map<String, String> inputData) {
		Map<String, String> header = new HashMap<>();
		header.put(inputData.get("edcApiKey"), inputData.get("edcApiKeyValue"));
		return header;
	}

	@SneakyThrows
	public String createAsset(Customer customerDetails, Map<String, String> inputData) {

		String dataURL = inputData.get(CONTROL_PLANE_DATA_ENDPOINT);
		String uId = UUID.randomUUID().toString();
		String orgName = customerDetails.getOrganizationName();
		String baseUrl = inputData.get("dtregistryUrl");

		String readValueAsTree = getSchemaFromFile("/resources/edc-request-template/asset.json");
		String jsonString = String.format(readValueAsTree, uId, uId, orgName, baseUrl, baseUrl);

		ObjectNode json = (ObjectNode) new ObjectMapper().readTree(jsonString);
		eDCApiProxy.createAsset(new URI(dataURL), requestHeader(inputData), json);

		return uId;
	}

	@SneakyThrows
	public String createPolicy(Customer customerDetails, Map<String, String> inputData) {

		String uId = UUID.randomUUID().toString();

		String readValueAsTree = getSchemaFromFile("/resources/edc-request-template/policy.json");
		String jsonString = String.format(readValueAsTree, uId);

		String dataURL = inputData.get(CONTROL_PLANE_DATA_ENDPOINT);
		ObjectNode json = (ObjectNode) new ObjectMapper().readTree(jsonString);
		eDCApiProxy.createPolicy(new URI(dataURL), requestHeader(inputData), json);
		return uId;
	}

	@SneakyThrows
	public String createContractDefination(Customer customerDetails, Map<String, String> inputData, String assetId,
			String policyId) {
		String uId = UUID.randomUUID().toString();

		String readValueAsTree = getSchemaFromFile("/resources/edc-request-template/contract-defination.json");
		String jsonString = String.format(readValueAsTree, uId, policyId, policyId, assetId);

		String dataURL = inputData.get(CONTROL_PLANE_DATA_ENDPOINT);
		ObjectNode json = (ObjectNode) new ObjectMapper().readTree(jsonString);
		eDCApiProxy.createContractDefination(new URI(dataURL), requestHeader(inputData), json);
		return uId;
	}

	@SneakyThrows
	private String getSchemaFromFile(String schemaFile) {
		JsonParser createParser = null;
		try {
			MappingJsonFactory jf = new MappingJsonFactory();
			InputStream jsonFile = this.getClass().getResourceAsStream(schemaFile);
			createParser = jf.createParser(jsonFile);
			return createParser.getValueAsString();
		} finally {
			if (createParser != null)
				createParser.close();
		}
	}

}
