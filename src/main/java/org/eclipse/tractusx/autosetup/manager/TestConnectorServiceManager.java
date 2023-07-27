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

package org.eclipse.tractusx.autosetup.manager;

import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.testservice.proxy.ConnectorTestRequest;
import org.eclipse.tractusx.autosetup.testservice.proxy.ConnectorTestServiceProxy;
import org.eclipse.tractusx.autosetup.testservice.proxy.ConnectorTestServiceResponse;
import org.eclipse.tractusx.autosetup.utility.WaitingTimeUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestConnectorServiceManager {

	private final AutoSetupTriggerManager autoSetupTriggerManager;

	private final ConnectorTestServiceProxy connectorTestServiceProxy;

	@Value("${connector.test.service.url}")
	private String connectorTestServiceURL;

	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "#{${retry.backOffDelay}}"))
	public Map<String, String> verifyConnectorTestingThroughTestService(Customer customerDetails,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("CONNECTOR_TEST_SERVICE").build();
		try {

			ConnectorTestRequest connectorTestRequest = ConnectorTestRequest.builder()
					.apiKeyHeader(inputData.get("edcApiKey")).apiKeyValue(inputData.get("edcApiKeyValue"))
					.connectorHost(inputData.get("controlPlaneEndpoint")).build();

			inputData.put("testServiceURL", connectorTestServiceURL);

			WaitingTimeUtility.waitingTime("Waiting after connector setup to get pod up to test connector as data provider/consumer");

			ConnectorTestServiceResponse testResult = connectorTestServiceProxy
					.verifyConnectorTestingThroughTestService(connectorTestRequest);

			log.info("Connector status: " + testResult.getMessage());

			inputData.put("connectorTestResult", testResult.getMessage());

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			inputData.put("connectorTestResult", "The automatic test wasn't successfully completed");
			
			log.error("ConnectorTestService failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("ConnectorTestService Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return inputData;
	}


}
