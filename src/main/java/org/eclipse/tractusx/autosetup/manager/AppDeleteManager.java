package org.eclipse.tractusx.autosetup.manager;

import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.constant.AppNameConstant;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppDeleteManager {

	private final KubeAppsPackageManagement appManagement;
	private final AutoSetupTriggerManager autoSetupTriggerManager;

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public Map<String, String> deletePackage(AppNameConstant app, SelectedTools tool, Map<String, String> inputData,
			AutoSetupTriggerEntry triger) {

		String packageName = tool.getLabel();
		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step(app.name() + "-" + packageName).build();
		try {
			appManagement.deletePackage(app, packageName, inputData);

		} catch (Exception e) {

			log.error("DeletePackageManager failed retry attempt: : {}, Error: {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1, e.getMessage());

			if (!e.getMessage().contains("404"))
				throw new ServiceException(
						"Error in " + packageName + "-" + app.name() + " package delete " + e.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}

		return inputData;
	}

}
