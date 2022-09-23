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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.autosetup.entity.AutoSetupTriggerEntry;
import com.autosetup.model.Customer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InputConfigurationManager {

	@Value("${target.cluster}")
	private String targetCluster;

	@Value("${target.namespace}")
	private String targetNamespace;

	@Value("${dns.name}")
	private String dnsOriginalName;

	@Value("${dns.name.protocol}")
	private String dnsNameURLProtocol;

	public Map<String, String> prepareInputConfiguration(Customer customerDetails, String uuid) {

		String targetNamespace = buildTargetNamespace(customerDetails.getOrganizationName(), uuid);

		String dnsName = buildDnsName(customerDetails, targetNamespace);

		Map<String, String> inputConfiguration = new ConcurrentHashMap<>();

		inputConfiguration.put("dnsName", dnsName);
		inputConfiguration.put("dnsNameURLProtocol", dnsNameURLProtocol);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);
		
		if (customerDetails.getProperties() != null) {
			inputConfiguration.put("bpnNumber", customerDetails.getProperties().get("bpnNumber"));

			if (customerDetails.getProperties().containsKey("role")) {
				String role = customerDetails.getProperties().get("role");
				inputConfiguration.put("role", role);
			}
		}

		return inputConfiguration;
	}

	public Map<String, String> prepareInputFromDBObject(AutoSetupTriggerEntry triggerEntry) {

		String targetNamespace = triggerEntry.getAutosetupTenantName();

		Map<String, String> inputConfiguration = new ConcurrentHashMap<>();

		inputConfiguration.put("dnsName", "");
		inputConfiguration.put("dnsNameURLProtocol", dnsNameURLProtocol);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespace);

		return inputConfiguration;
	}

	private int findIndexOfCharatcer(String str, int count) {
		int index = 1;
		while (count > 0) {
			index = str.indexOf("-", index + 1);
			count--;
		}
		return index;
	}

	private String buildDnsName(Customer customerDetails, String targetNamespace) {
		targetNamespace = targetNamespace.substring(0, findIndexOfCharatcer(targetNamespace, 1));
		String country = customerDetails.getCountry();
		country = country.replaceAll("[^a-zA-Z0-9]", "");
		return dnsOriginalName.replace("tenantname", targetNamespace + "-" + country.toLowerCase());
	}

	private String buildTargetNamespace(String orgName, String uuid) {

		int tenantNameLength = 6;
		String tenantName = orgName.replaceAll("[^a-zA-Z0-9]", "");
		tenantName = tenantName.length() < tenantNameLength ? tenantName : tenantName.substring(0, tenantNameLength);
		// uuid = uuid.replaceAll("[^a-zA-Z]", "");
		return tenantName.concat("-" + uuid).toLowerCase();
	}

}
