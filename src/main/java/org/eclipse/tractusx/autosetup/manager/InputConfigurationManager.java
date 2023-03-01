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

package org.eclipse.tractusx.autosetup.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.model.AutoSetupRequest;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.CustomerProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InputConfigurationManager {

	@Value("${target.cluster}")
	private String targetCluster;

	@Value("${target.namespace}")
	private String targetNamespace;

	@Value("${dns.name}")
	private String dnsOriginalName;

	@Value("${dns.name.protocol}")
	private String dnsNameURLProtocol;

	public Map<String, String> prepareInputConfiguration(AutoSetupRequest autoSetupRequest, String uuid) {

		Customer customerDetails = autoSetupRequest.getCustomer();
		CustomerProperties customerProp = autoSetupRequest.getProperties();

		String targetNamespaceString = buildTargetNamespace(customerDetails.getOrganizationName(), uuid);

		String dnsName = buildDnsName(customerDetails, uuid);

		Map<String, String> inputConfiguration = new ConcurrentHashMap<>();

		inputConfiguration.put("dnsName", dnsName);
		inputConfiguration.put("dnsNameURLProtocol", dnsNameURLProtocol);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespaceString);

		if (customerProp != null) {

			inputConfiguration.put("bpnNumber", customerProp.getBpnNumber());
			inputConfiguration.put("subscriptionId", customerProp.getSubscriptionId());
			inputConfiguration.put("serviceId", customerProp.getServiceId());

			if (customerProp.getRole() != null) {
				String role = customerProp.getRole();
				inputConfiguration.put("role", role);
			}
		}

		return inputConfiguration;
	}

	public Map<String, String> prepareInputFromDBObject(AutoSetupTriggerEntry triggerEntry) {

		String targetNamespaceString = triggerEntry.getAutosetupTenantName();

		Map<String, String> inputConfiguration = new ConcurrentHashMap<>();

		inputConfiguration.put("dnsName", "");
		inputConfiguration.put("dnsNameURLProtocol", dnsNameURLProtocol);
		inputConfiguration.put("targetCluster", targetCluster);
		inputConfiguration.put("targetNamespace", targetNamespaceString);

		return inputConfiguration;
	}

	private int findIndexOfCharatcer(String str, String character, int count) {
		int index = 1;
		while (count > 0) {
			try {
				index = str.indexOf(character, index + 1);
				count--;
			} catch (Exception e) {
				log.debug("Unable to generate expected namespace/dsnname" + e.getMessage());
				index = str.length() < index ? str.length() : index;
			}
		}
		return index == -1 ? str.length() : index;
	}

	private String buildDnsName(Customer customerDetails, String uuid) {

		String tenantName = getTenantName(customerDetails.getOrganizationName());
		uuid = uuid.substring(0, findIndexOfCharatcer(uuid, "-", 1)).toLowerCase();
		String country = customerDetails.getCountry();
		country = country.replaceAll("[^a-zA-Z0-9]", "");
		return dnsOriginalName.replace("tenantname", tenantName + "-" + uuid + "-" + country).toLowerCase();
	}

	private String buildTargetNamespace(String orgName, String uuid) {

		String tenantName = getTenantName(orgName);
		String targetNamespaceVariable = dnsOriginalName.substring(0, findIndexOfCharatcer(dnsOriginalName, ".", 2))
				.toLowerCase();
		targetNamespaceVariable = targetNamespaceVariable.replace("tenantname.", "");
		return tenantName.concat("-" + targetNamespaceVariable + "-" + uuid).toLowerCase();
	}

	private String getTenantName(String orgName) {
		int tenantNameLength = 6;
		String tenantName = orgName.replaceAll("[^a-zA-Z0-9]", "");
		tenantName = tenantName.length() < tenantNameLength ? tenantName : tenantName.substring(0, tenantNameLength);
		return tenantName;
	}

}
