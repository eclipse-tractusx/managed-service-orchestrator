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

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> verifyConnectorTestingThroughTestService(Customer customerDetails,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("CONNECTOR_TEST_SERVICE").build();
		try {

			ConnectorTestRequest connectorTestRequest = ConnectorTestRequest.builder()
					.apiKeyHeader(inputData.get("edcApiKey")).apiKeyValue(inputData.get("edcApiKeyValue"))
					.connectorHost(inputData.get("controlPlaneEndpoint")).build();

			String testResult = connectorTestServiceProxy
					.verifyConnectorTestingThroughTestService(connectorTestRequest);

			inputData.put("connectorTestResult", testResult);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

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
