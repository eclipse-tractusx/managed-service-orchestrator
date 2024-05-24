/********************************************************************************
 * Copyright (c) 2023,2024 T-Systems International GmbH
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.utility.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConnectorCommonUtilityManager {

	@Value("${edc.miwUrl:default}")
	private String edcMiwUrl;

	@Value("${sde.keycloak-tokenUrl:default}")
	private String sdeKeycloakTokenUrl;

	@Value("${edc.ssi.authorityId:}")
	private String authorityId;
	
	@Value("${edc.iatp.id}")
	private String edcIatpId;
	
	@Value("${edc.iatp.sts.dim.url}")
	private String edcIatpStsDimUrl;
	
	@Value("${edc.iatp.sts.oauth.token.url}")
	private String edcIatpStsOauthTokenUrl;
	
	@Value("${edc.bdrs.server}")
	private String edcBdrsServer;
	
	@Value("${edc.iam.trusted-issuer}")
	private String edcIamTrustedIssuer;
	
	public Map<String, String> prepareConnectorInput(Customer customerDetails, String packageName, Map<String, String> inputData) {

		String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
		String dnsName = inputData.get("dnsName");
		String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

		String controlplaneurl = dnsNameURLProtocol + "://" + dnsName;

		inputData.put("dataPlanePublicUrl",
				dnsNameURLProtocol + "://" + packageName + "-edcdataplane-edc-dataplane:8185/api/public");

		String localControlplane = dnsNameURLProtocol + "://" + packageName
				+ "-edccontrolplane-edc-controlplane:8182/validation/token";
		
		String dataPlanePublicEndpoint = controlplaneurl + "/api/public";

		inputData.put("controlPlaneValidationEndpoint", localControlplane);

		inputData.put("controlPlaneEndpoint", controlplaneurl);
		inputData.put("controlPlaneDataEndpoint", controlplaneurl + "/data");
		inputData.put("edcApiKey", "X-Api-Key");
		inputData.put("edcApiKeyValue", generateRandomPassword);
		inputData.put("controlPlaneIdsEndpoint", controlplaneurl + "/api/v1/ids/data");
		inputData.put("dataplaneendpoint", controlplaneurl);
		inputData.put("dataPlanePublicEndpoint", dataPlanePublicEndpoint);

		String dftAddress = dnsNameURLProtocol + "://" + dnsName + "/backend/api";
		inputData.put("dftAddress", dftAddress);

		inputData.put("keycloakAuthTokenURL", sdeKeycloakTokenUrl);
		inputData.put("edcMiwUrl", edcMiwUrl);

		if (StringUtils.isBlank(authorityId))
			inputData.put("authorityId", inputData.get("bpnNumber"));
		else
			inputData.put("authorityId", authorityId);

		inputData.put("postgresPassword", "admin@123");
		inputData.put("username", "admin");
		inputData.put("appdbpass", "admin@123");

		String edcDb = "jdbc:postgresql://" + packageName + "-postgresdb-postgresql:5432/postgres";
		inputData.put("edcdatabaseurl", edcDb);
		
		String bpnNumber = inputData.get("bpnNumber");
		String lowercaseCompanyFormatedName = customerDetails.getOrganizationName().toLowerCase().replace(" ", "--");
		String edcIatpStsOauthTokenUrlformated = edcIatpStsOauthTokenUrl.replace("bpnl", bpnNumber.toLowerCase());
		edcIatpStsOauthTokenUrlformated = edcIatpStsOauthTokenUrlformated.replace("companyname",
				lowercaseCompanyFormatedName);

		inputData.put("iatp.id", edcIatpId + bpnNumber);
		inputData.put("iatp.sts.dim.url", edcIatpStsDimUrl);
		inputData.put("iatp.sts.oauth.client.id", inputData.get("dimClientId"));
		inputData.put("iatp.sts.oauth.token_url", edcIatpStsOauthTokenUrlformated);
		inputData.put("bdrs.server", edcBdrsServer);
		inputData.put("edc.iam.trusted-issuer", edcIamTrustedIssuer);
		inputData.put("dataplane.token.refresh.refresh_endpoint", dataPlanePublicEndpoint + "/token");

		return inputData;
	}
}
