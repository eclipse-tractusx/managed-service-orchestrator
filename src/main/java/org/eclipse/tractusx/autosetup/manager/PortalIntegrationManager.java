/********************************************************************************
 * Copyright (c) 2022,2024 T-Systems International GmbH
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerDetails;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.NoDataFoundException;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.exception.ValidationException;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.portal.model.ServiceInstanceResultRequest;
import org.eclipse.tractusx.autosetup.portal.model.ServiceInstanceResultResponse;
import org.eclipse.tractusx.autosetup.portal.model.TechnicalUserDetails;
import org.eclipse.tractusx.autosetup.portal.model.TechnicalUsers;
import org.eclipse.tractusx.autosetup.portal.proxy.PortalIntegrationProxy;
import org.eclipse.tractusx.autosetup.utility.KeyCloakTokenProxyUtitlity;
import org.eclipse.tractusx.autosetup.utility.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortalIntegrationManager {

	private static final String AUTHORIZATION = "Authorization";

	private static final String BEARER = "Bearer ";

	private final PortalIntegrationProxy portalIntegrationProxy;

	private final AutoSetupTriggerManager autoSetupTriggerManager;

	private final KeyCloakTokenProxyUtitlity keyCloakTokenProxyUtitlity;

	@Value("${portal.url}")
	private URI portalUrl;

	@Value("${portal.keycloak.clientId}")
	private String clientId;

	@Value("${portal.keycloak.clientSecret}")
	private String clientSecret;

	@Value("${portal.keycloak.tokenURI}")
	private URI tokenURI;

	@Value("${portal.request.timeout:20000}")
	private int requestTimeout;

	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "#{${retry.backOffDelay}}"))
	public Map<String, String> postServiceInstanceResultAndGetTenantSpecs(Customer customerDetails, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("PostServiceInstanceResultAndGetTenantSpecs").build();
		ServiceInstanceResultResponse serviceInstanceResultResponse = null;
		try {
			String appServiceURIPath = "apps";

			if (!"app".equalsIgnoreCase(tool.getType())) {
				appServiceURIPath = "services";
			}

			String packageName = tool.getLabel();
			String tenantName = customerDetails.getOrganizationName();

			log.info(LogUtil.encode(tenantName) + "-" + LogUtil.encode(packageName)
					+ "-PostServiceInstanceResultAndGetTenantSpecs creating");
			String dnsName = inputData.get("dnsName");
			String dnsNameURLProtocol = inputData.get("dnsNameURLProtocol");
			String subscriptionId = inputData.get("subscriptionId");
			String offerId = inputData.get("serviceId");

			String applicationURL = dnsNameURLProtocol + "://" + dnsName;
			inputData.put("applicationURL", applicationURL);

			ServiceInstanceResultRequest serviceInstanceResultRequest = ServiceInstanceResultRequest.builder()
					.requestId(subscriptionId).offerUrl(applicationURL).build();

			serviceInstanceResultResponse = processAppServiceGetResponse(subscriptionId, offerId,
					serviceInstanceResultRequest, appServiceURIPath);

			handlePortalServiceExcutionResponse(inputData, autoSetupTriggerDetails, serviceInstanceResultResponse);

			log.info(LogUtil.encode(tenantName) + "-" + LogUtil.encode(packageName)
					+ "-PostServiceInstanceResultAndGetTenantSpecs created");

		} catch (NoDataFoundException e) {
			log.error(LogUtil.encode(
					"PortalIntegrationManager NoDataFoundException failed No retry attempt: : " + e.getMessage()));
			throw e;
		} catch (FeignException e) {

			log.error(LogUtil.encode("PortalIntegrationManager FeignException failed retry attempt: : "
					+ RetrySynchronizationManager.getContext().getRetryCount() + 1));
			log.error(LogUtil.encode("RequestBody: " + e.request()));
			log.error(LogUtil.encode("ResponseBody: " + e.contentUTF8()));

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(e.contentUTF8());
			throw new ServiceException("PortalIntegrationManager Oops! We have an FeignException - " + e.contentUTF8());

		} catch (Exception ex) {

			log.error(LogUtil.encode("PortalIntegrationManager Exception failed retry attempt: : "
					+ RetrySynchronizationManager.getContext().getRetryCount() + 1));

			if (serviceInstanceResultResponse != null) {
				String msg = "PortalIntegrationManager failed with details:"
						+ serviceInstanceResultResponse.toJsonString();
				log.error(msg);
				autoSetupTriggerDetails.setRemark(msg);
			} else
				autoSetupTriggerDetails.setRemark(ex.getMessage());

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());

			throw new ServiceException("PortalIntegrationManager Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}
		return inputData;
	}
	
	
	@Retryable(retryFor = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "#{${retry.backOffDelay}}"))
	public Map<String, String> activateAppAndServiceInstanceOnPortalEnd(Customer customerDetails, SelectedTools tool,
			Map<String, String> inputData, AutoSetupTriggerEntry triger) {

		AutoSetupTriggerDetails autoSetupTriggerDetails = AutoSetupTriggerDetails.builder()
				.id(UUID.randomUUID().toString()).step("ActivateAppAndServiceInstanceOnPortalEnd").build();
		try {
			String appServiceURIPath = "apps";

			if (!"app".equalsIgnoreCase(tool.getType())) {
				appServiceURIPath = "services";
			}

			String packageName = tool.getLabel();
			String tenantName = customerDetails.getOrganizationName();

			log.info(LogUtil.encode(tenantName) + "-" + LogUtil.encode(packageName)
					+ "-ActivateAppAndServiceInstanceOnPortalEnd acivating");
			String subscriptionId = inputData.get("subscriptionId");

			portalIntegrationProxy.activateAppServiceOnPortalSide(
					portalUrl, getHeaderWithToken(), appServiceURIPath, subscriptionId);
			
			log.info(LogUtil.encode(tenantName) + "-" + LogUtil.encode(packageName)
					+ "-ActivateAppAndServiceInstanceOnPortalEnd acivated");

		} catch (FeignException e) {

			log.error(LogUtil.encode("PortalIntegrationManager-activateAppAndServiceInstanceOnPortalEnd FeignException failed retry attempt: : "
					+ RetrySynchronizationManager.getContext().getRetryCount() + 1));
			log.error(LogUtil.encode("RequestBody: " + e.request()));
			log.error(LogUtil.encode("ResponseBody: " + e.contentUTF8()));

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());
			autoSetupTriggerDetails.setRemark(e.contentUTF8());
			throw new ServiceException("PortalIntegrationManager-activateAppAndServiceInstanceOnPortalEnd Oops! We have an FeignException - " + e.contentUTF8());

		} catch (Exception ex) {

			log.error(LogUtil.encode("PortalIntegrationManager-activateAppAndServiceInstanceOnPortalEnd Exception failed retry attempt: : "
					+ RetrySynchronizationManager.getContext().getRetryCount() + 1));

			autoSetupTriggerDetails.setStatus(TriggerStatusEnum.FAILED.name());

			ex.getStackTrace();
			
			throw new ServiceException("PortalIntegrationManager-activateAppAndServiceInstanceOnPortalEnd Oops! We have an exception - " + ex.getMessage());
		} finally {
			autoSetupTriggerManager.saveTriggerDetails(autoSetupTriggerDetails, triger);
		}
		
		return inputData;
	}

	@SneakyThrows
	private void handlePortalServiceExcutionResponse(Map<String, String> inputData,
			AutoSetupTriggerDetails autoSetupTriggerDetails,
			ServiceInstanceResultResponse serviceInstanceResultResponse) {

		if (serviceInstanceResultResponse != null) {

			inputData.put("keycloakResourceClient", serviceInstanceResultResponse.getAppInstanceId());

			autoSetupTriggerDetails.setRemark(serviceInstanceResultResponse.toJsonString());

			List<TechnicalUsers> technicalUserData = serviceInstanceResultResponse.getTechnicalUserData();

			if (technicalUserData != null && !technicalUserData.isEmpty()) {

				if (technicalUserData.size() > 2) {
					throw new ValidationException("We have recieved more than two tehcnical users from portal");
				}

				technicalUserData.forEach(technicalUser -> {
					TechnicalUserDetails technicalUserDetails = technicalUser.getTechnicalUserDetails();
					if (technicalUser.getPermissions().contains("Identity Wallet Management")) {
						inputData.put("dimClientId", technicalUserDetails.getClientId());
						inputData.put("dimClientSecret", technicalUserDetails.getSecret());
					} else {
						inputData.put("keycloakAuthenticationClientId", technicalUserDetails.getClientId());
						inputData.put("keycloakAuthenticationClientSecret", technicalUserDetails.getSecret());
					}
				});

			} else {
				throw new NoDataFoundException("Technical users is null or empty recieved from Portal");
			}
		} else {
			throw new NoDataFoundException("Error in request process with portal");
		}

	}

	@SneakyThrows
	private ServiceInstanceResultResponse processAppServiceGetResponse(String subscriptionId, String offerId,
			ServiceInstanceResultRequest serviceInstanceResultRequest, String appServiceURIPath) {

		ServiceInstanceResultResponse serviceInstanceResultResponse = readStatus(subscriptionId, offerId, appServiceURIPath);

		if (serviceInstanceResultResponse == null || serviceInstanceResultResponse.getTenantUrl() == null) {
			log.info("Posting app/service instanceURL becasue no tenant url preconfiguration found");
			portalIntegrationProxy.postAppServiceStartAutoSetup(portalUrl, getHeaderWithToken(), appServiceURIPath,
					serviceInstanceResultRequest);
			log.info("Post App/Service instanceURL, going to read credentials asynchronously");
		} 
		
		serviceInstanceResultResponse = verifyIsAlreadySubcribedActivatedAndGetDetails(subscriptionId, offerId,
				appServiceURIPath);

		if (serviceInstanceResultResponse == null || serviceInstanceResultResponse.getTechnicalUserData() == null
				|| serviceInstanceResultResponse.getTechnicalUserData().isEmpty()) {
			throw new ServiceException("Unable to read technical user detials from portal auto setup");
		}

		readTechnicalUserDetails(serviceInstanceResultResponse);

		return serviceInstanceResultResponse;
	}


	@SneakyThrows
	private ServiceInstanceResultResponse verifyIsAlreadySubcribedActivatedAndGetDetails(String subscriptionId,
			String offerId, String appServiceURIPath) {

		int retry = 5;
		int counter = 1;
		ServiceInstanceResultResponse serviceInstanceResultResponse = null;
		boolean continueCheckingifTenantUrlNotConfigureAndNotTechnicalUserCreated= true;
		do {
			log.info("Waiting '" + requestTimeout
					+ "'sec to portal /provider API call to get subcription status- retry => " + counter);
			Thread.sleep(requestTimeout);
			
			serviceInstanceResultResponse = readStatus(subscriptionId, offerId, appServiceURIPath);
			
			if (serviceInstanceResultResponse!=null && serviceInstanceResultResponse.getTenantUrl() != null
					&& !serviceInstanceResultResponse.getTechnicalUserData().isEmpty()) {
				continueCheckingifTenantUrlNotConfigureAndNotTechnicalUserCreated = false;
			}
			
			counter++;

		} while (continueCheckingifTenantUrlNotConfigureAndNotTechnicalUserCreated && counter <= retry);

		return serviceInstanceResultResponse;
	}

	private ServiceInstanceResultResponse readStatus(String subscriptionId, String offerId, String appServiceURIPath) {
		
		ServiceInstanceResultResponse serviceInstanceResultResponse =null;
		String offerSubscriptionStatus;
		try {
			serviceInstanceResultResponse = portalIntegrationProxy.getAppServiceInstanceSubcriptionDetails(
					portalUrl, getHeaderWithToken(), appServiceURIPath, offerId, subscriptionId);

			offerSubscriptionStatus = serviceInstanceResultResponse.getOfferSubscriptionStatus();
			log.info(LogUtil
					.encode("VerifyIsAlreadySubcribedActivatedAndGetDetails: The subscription details found for "
							+ offerId + ", " + subscriptionId + ", status is " + offerSubscriptionStatus
							+ ", result is " + serviceInstanceResultResponse.toJsonString()));
			
		} catch (FeignException e) {
			log.error(LogUtil.encode(
					"VerifyIsAlreadySubcribedActivatedAndGetDetails FeignException request: " + e.request()));
			log.error(LogUtil.encode("VerifyIsAlreadySubcribedActivatedAndGetDetails FeignException response Body: "
					+ e.responseBody()));
			String error = e.contentUTF8();
			error = StringUtils.isAllEmpty(error) ? error : e.getMessage();

			if (e.status() == 404) {
				log.warn(LogUtil.encode(
						"VerifyIsAlreadySubcribedActivatedAndGetDetails: The no app or subscription found for "
								+ offerId + ", " + subscriptionId + ", result is " + error));
			} else {
				log.error(LogUtil
						.encode("VerifyIsAlreadySubcribedActivatedAndGetDetails FeignException Exception response: "
								+ error));
			}

		} catch (Exception e) {
			log.error(LogUtil
					.encode("VerifyIsAlreadySubcribedActivatedAndGetDetails Exception processing portal call "
							+ e.getMessage()));
		}
		return serviceInstanceResultResponse;
	}

	@SneakyThrows
	private void readTechnicalUserDetails(ServiceInstanceResultResponse serviceInstanceResultResponse) {

		Map<String, String> header = getHeaderWithToken();

		serviceInstanceResultResponse.getTechnicalUserData().forEach(elel -> {
			try {
				TechnicalUserDetails technicalUserDetails = portalIntegrationProxy.getTechnicalUserDetails(portalUrl,
						header, elel.getId());
				elel.setTechnicalUserDetails(technicalUserDetails);
			} catch (FeignException e) {
				log.error("ReadTechnicalUserDetails FeignException request: " + e.request());
				log.error("ReadTechnicalUserDetails FeignException response Body: " + e.responseBody());
				String error = e.contentUTF8();
				error = StringUtils.isNotBlank(error) ? error : e.getMessage();
				log.error("ReadTechnicalUserDetails FeignException Exception response: " + error);
				if (e.status() == 409)
					throw new NoDataFoundException(error);
				else
					throw new ServiceException(error);
			} catch (Exception e) {
				String error = "Error in read existing TechnicalUserDetails from portal " + e.getMessage();
				log.error(error);
				throw new ServiceException(error);
			}
		});

	}
	
	private Map<String, String> getHeaderWithToken() {
		Map<String, String> header = new HashMap<>();
		header.put(AUTHORIZATION,
				BEARER + keyCloakTokenProxyUtitlity.getKeycloakToken(clientId, clientSecret, tokenURI));
		return header;
	}

}
