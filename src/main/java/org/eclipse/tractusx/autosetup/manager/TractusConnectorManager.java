package org.eclipse.tractusx.autosetup.manager;

import static org.eclipse.tractusx.autosetup.constant.AppNameConstant.TRACTUS_CONNECTOR;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.utility.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TractusConnectorManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Value("${daps.url}")
	private String dapsurl;

	@Value("${daps.jskurl}")
	private String dapsjsksurl;

	@Value("${daps.token.url}")
	private String dapstokenurl;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> managePackage(Customer customerDetails, AppActions action, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		Map<String, String> outputData = new HashMap<>();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(TRACTUS_CONNECTOR.name()).build();
		try {
			String packageName = tool.getLabel();

			String generateRandomPassword = PasswordGenerator.generateRandomPassword(50);
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");

			String controlplaneurl = dnsNameURLProtocol + "://" + dnsName;

			inputData.put("dapsurl", dapsurl);
			inputData.put("dapsjsksurl", dapsjsksurl);
			inputData.put("dapstokenurl", dapstokenurl);

			inputData.put("dataPlanePublicUrl",
					dnsNameURLProtocol + "://" + packageName + "-edcdataplane-edc-dataplane:8185/api/public");
			String localControlplane = dnsNameURLProtocol + "://" + packageName
					+ "-edccontrolplane-edc-controlplane:8182/validation/token";

			outputData.put("controlPlaneValidationEndpoint", localControlplane);

			outputData.put("controlPlaneEndpoint", controlplaneurl);
			outputData.put("controlPlaneDataEndpoint", controlplaneurl + "/data");
			outputData.put("edcApiKey", "X-Api-Key");
			outputData.put("edcApiKeyValue", generateRandomPassword);
			outputData.put("controlPlaneIdsEndpoint", controlplaneurl + "/api/v1/ids/data");
			outputData.put("dataplaneendpoint", controlplaneurl);
			outputData.put("dataPlanePublicEndpoint", controlplaneurl + "/public");

			inputData.putAll(outputData);

			String dftAddress = dnsNameURLProtocol + "://" + dnsName + "/dftbackend/api";
			inputData.put("dftAddress", dftAddress);

			String edcDb = "jdbc:postgresql://" + packageName + "-postgresdb-postgresql:5432/postgres";
			inputData.put("edcdatabaseurl", edcDb);

			if (AppActions.CREATE.equals(action))
				appManagement.createPackage(TRACTUS_CONNECTOR, packageName, inputData);
			else
				appManagement.updatePackage(TRACTUS_CONNECTOR, packageName, inputData);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.SUCCESS.name());

		} catch (Exception ex) {

			log.error("TractusConnectorManager failed retry attempt: : {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1);

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(ex.getMessage());
			throw new ServiceException("TractusConnectorManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return outputData;
	}

}
