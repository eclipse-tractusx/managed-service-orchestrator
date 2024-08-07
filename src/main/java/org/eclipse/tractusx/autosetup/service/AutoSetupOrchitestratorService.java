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

package org.eclipse.tractusx.autosetup.service;

import static org.eclipse.tractusx.autosetup.constant.AppActions.CREATE;
import static org.eclipse.tractusx.autosetup.constant.AppActions.DELETE;
import static org.eclipse.tractusx.autosetup.constant.AppActions.UPDATE;
import static org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum.INPROGRESS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.tractusx.autosetup.constant.AppActions;
import org.eclipse.tractusx.autosetup.constant.TriggerStatusEnum;
import org.eclipse.tractusx.autosetup.entity.AppServiceCatalogAndCustomerMapping;
import org.eclipse.tractusx.autosetup.entity.AutoSetupTriggerEntry;
import org.eclipse.tractusx.autosetup.exception.NoDataFoundException;
import org.eclipse.tractusx.autosetup.exception.ServiceException;
import org.eclipse.tractusx.autosetup.exception.ValidationException;
import org.eclipse.tractusx.autosetup.kubeapps.proxy.KubeAppManageProxy;
import org.eclipse.tractusx.autosetup.manager.AutoSetupTriggerManager;
import org.eclipse.tractusx.autosetup.manager.EmailManager;
import org.eclipse.tractusx.autosetup.manager.InputConfigurationManager;
import org.eclipse.tractusx.autosetup.mapper.AutoSetupRequestMapper;
import org.eclipse.tractusx.autosetup.mapper.AutoSetupTriggerMapper;
import org.eclipse.tractusx.autosetup.model.AutoSetupRequest;
import org.eclipse.tractusx.autosetup.model.Customer;
import org.eclipse.tractusx.autosetup.model.SelectedTools;
import org.eclipse.tractusx.autosetup.repository.AutoSetupTriggerEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoSetupOrchitestratorService {

	private static final String SUCCESS_HTML_TEMPLATE = "success.html";
	private static final String CONTENT = "content";
	private static final String CCEMAIL = "ccemail";
	private static final String TEST_SERVICE_URL = "testServiceURL";
	private static final String CONNECTOR_TEST_RESULT = "connectorTestResult";
	private static final String EMAIL_SENT_SUCCESSFULLY = "Email sent successfully";
	private static final String TOEMAIL = "toemail";
	private static final String ORGNAME = "orgname";
	public static final String TARGET_NAMESPACE = "targetNamespace";
	public static final String SDE_FRONTEND_URL = "sdeFrontEndUrl";
	public static final String SDE_BACKEND_URL = "sdeBackEndUrl";

	private final KubeAppManageProxy kubeAppManageProxy;
	private final AutoSetupTriggerManager autoSetupTriggerManager;
	private final AutoSetupRequestMapper customerDetailsMapper;
	private final EDCConnectorWorkFlow edcConnectorWorkFlow;
	private final SDEAppWorkFlow sdeWorkFlow;
	private final DTAppWorkFlow dtAppWorkFlow;

	private final InputConfigurationManager inputConfigurationManager;

	private final AutoSetupTriggerMapper autoSetupTriggerMapper;
	private final AutoSetupRequestMapper autoSetupRequestMapper;

	@Autowired
	private AutoSetupTriggerEntryRepository autoSetupTriggerEntryRepository;

	@Autowired
	private AppDetailsService appDetailsService;

	@Autowired
	private EmailManager emailManager;

	@Value("${target.cluster}")
	private String targetCluster;

	@Value("${portal.email.address}")
	private String technicalEmail;

	@Value("${mail.replyto.address}")
	private String mailReplytoAddress;

	@Value("${manual.update}")
	private boolean manualUpdate;

	@Value("${managed.dt-registry:true}")
	private boolean managedDtRegistry;
	
	@Value("${managed.dt-registry.local:true}")
	private boolean managedDTRegistryLocal;

	public String getAllInstallPackages() {
		return kubeAppManageProxy.getAllInstallPackages();
	}

	ObjectMapper mapper = new ObjectMapper();

	public String createPackage(AutoSetupRequest autoSetupRequest) {

		String uuID = UUID.randomUUID().toString();

		Map<String, String> inputConfiguration = inputConfigurationManager.prepareInputConfiguration(autoSetupRequest,
				uuID);

		String targetNamespace = inputConfiguration.get(TARGET_NAMESPACE);

		AutoSetupTriggerEntry trigger = autoSetupTriggerManager.createTrigger(autoSetupRequest, CREATE, uuID,
				targetNamespace);
		try {
			List<AppServiceCatalogAndCustomerMapping> appCatalogDetails = verifyIsServiceValid(autoSetupRequest);

			Runnable runnable = () -> {

				if (!checkNamespaceisExist(targetNamespace)) {
					kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);
				}

				proceessTrigger(autoSetupRequest, CREATE, trigger, inputConfiguration, appCatalogDetails);
			};

			new Thread(runnable).start();
		} catch (Exception e) {
			log.error("Error in package creation process start: " + e.getMessage());
			trigger.setStatus(TriggerStatusEnum.FAILED.name());
			trigger.setRemark(e.getMessage());
			autoSetupTriggerManager.saveTriggerUpdate(trigger);
			throw e;
		}
		return uuID;
	}

	public String updatePackage(AutoSetupRequest autoSetupRequest, String triggerId) {

		AutoSetupTriggerEntry trigger = autoSetupTriggerEntryRepository.findAllByTriggerId(triggerId);

		if (trigger != null) {

			try {
				List<AppServiceCatalogAndCustomerMapping> appCatalogDetails = verifyIsServiceValid(autoSetupRequest);

				Map<String, String> inputConfiguration = inputConfigurationManager
						.prepareInputConfiguration(autoSetupRequest, triggerId);

				Runnable runnable = () -> {

					trigger.setTriggerType(DELETE.name());
					trigger.setStatus(INPROGRESS.name());

					autoSetupTriggerManager.saveTriggerUpdate(trigger);

					String targetNamespace = inputConfiguration.get(TARGET_NAMESPACE);

					String existingNamespace = trigger.getAutosetupTenantName();

					if (checkNamespaceisExist(existingNamespace)) {

						updateSubmethod(trigger, inputConfiguration, targetNamespace, existingNamespace);

					} else {
						trigger.setAutosetupTenantName(targetNamespace);
						kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);
					}

					AutoSetupTriggerEntry updatedtrigger = autoSetupTriggerManager
							.updateTriggerAutoSetupRequest(autoSetupRequest, trigger, UPDATE);

					proceessTrigger(autoSetupRequest, CREATE, updatedtrigger, inputConfiguration, appCatalogDetails);

				};

				new Thread(runnable).start();
			} catch (Exception e) {
				log.error("Error in package update process start :" + e.getMessage());
				trigger.setStatus(TriggerStatusEnum.FAILED.name());
				trigger.setRemark(e.getMessage());
				trigger.setAutosetupRequest(customerDetailsMapper.fromCustomer(autoSetupRequest));
				autoSetupTriggerManager.saveTriggerUpdate(trigger);
				throw e;
			}

		} else {
			throw new NoDataFoundException("No Valid Auto setup found for " + triggerId + " to update");
		}

		return triggerId;
	}

	private void updateSubmethod(AutoSetupTriggerEntry trigger, Map<String, String> inputConfiguration,
			String targetNamespace, String existingNamespace) {
		inputConfiguration.put(TARGET_NAMESPACE, existingNamespace);

		processDeleteTrigger(trigger, inputConfiguration);

		inputConfiguration.put(TARGET_NAMESPACE, targetNamespace);
		trigger.setAutosetupTenantName(targetNamespace);

		if (!existingNamespace.equals(targetNamespace) && !checkNamespaceisExist(targetNamespace)) {
			kubeAppManageProxy.createNamespace(targetCluster, targetNamespace);
		}
		try {
			log.info("Waiting after deleteing all package for recreate");
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private List<AppServiceCatalogAndCustomerMapping> verifyIsServiceValid(AutoSetupRequest autoSetupRequest) {

		// In future if want to support multiple service as installation we can make
		// changes here
		String serviceId = autoSetupRequest.getProperties().getServiceId();
		List<String> ids = Arrays.asList(serviceId);

		List<AppServiceCatalogAndCustomerMapping> findAllById = appDetailsService.findByServiceIds(ids);
		if (findAllById.isEmpty())
			throw new ValidationException("The service Id " + serviceId + " is not supported for auto-setup");

		return findAllById;
	}

	private List<SelectedTools> getToolInfo(AppServiceCatalogAndCustomerMapping appCatalog) {

		try {
			String jsonStr = appCatalog.getServiceCatalog().getServiceTools();

			if (jsonStr != null && !jsonStr.isEmpty()) {
				return mapper.readValue(jsonStr, new TypeReference<List<SelectedTools>>() {
				});
			}
		} catch (Exception e) {
			log.error("Error in parsing selected tools list");
		}
		return List.of();
	}

	public String deletePackage(String triggerId) {

		AutoSetupTriggerEntry trigger = autoSetupTriggerEntryRepository.findAllByTriggerId(triggerId);

		if (trigger != null) {

			Map<String, String> inputConfiguration = inputConfigurationManager.prepareInputFromDBObject(trigger);

			trigger.setAutosetupResult("");
			trigger.setTriggerType(DELETE.name());
			trigger.setStatus(INPROGRESS.name());

			AutoSetupTriggerEntry deleteTrigger = autoSetupTriggerManager.saveTriggerUpdate(trigger);

			Runnable runnable = () -> processDeleteTrigger(deleteTrigger, inputConfiguration);
			new Thread(runnable).start();
		} else {
			throw new NoDataFoundException("No Valid Auto setup found for " + triggerId + " to delete");
		}

		return triggerId;
	}

	private void proceessTrigger(AutoSetupRequest autoSetupRequest, AppActions action, AutoSetupTriggerEntry trigger,
			Map<String, String> inputConfiguration, List<AppServiceCatalogAndCustomerMapping> appCatalogListDetails) {

		try {

			Customer customer = autoSetupRequest.getCustomer();
			trigger.setTriggerType(action.name());

			for (AppServiceCatalogAndCustomerMapping appCatalogDetails : appCatalogListDetails) {

				List<SelectedTools> selectedTools = getToolInfo(appCatalogDetails);

				for (SelectedTools selectedTool : selectedTools) {

					switch (selectedTool.getTool()) {

					case SDE_WITH_EDC_TRACTUS:

						executeSDEWithEDCTractus(autoSetupRequest, action, trigger, inputConfiguration, customer,
								selectedTool);

						break;

					case EDC_TRACTUS:

						executeEDCTractus(autoSetupRequest, action, trigger, inputConfiguration, selectedTool);

						break;
					case DT_REGISTRY:

						dtDeployment(autoSetupRequest.getCustomer(), action, trigger, inputConfiguration, selectedTool);

						break;
					default:
						throw new ServiceException(selectedTool.getTool() + " is not supported for auto setup");
					}
				}

				log.info("All Packages created/updated successfully!!!!");
			}

		} catch (Exception e) {

			log.error("Error in package creation " + e.getMessage());
			trigger.setStatus(TriggerStatusEnum.FAILED.name());
			trigger.setRemark(e.getMessage());
			generateNotification(autoSetupRequest.getCustomer(),
					"Error in autosetup execution - " + trigger.getTriggerId(), "", SUCCESS_HTML_TEMPLATE);
		} finally {
			LocalDateTime now = LocalDateTime.now();
			trigger.setModifiedTimestamp(now.toString());
			trigger.setInputConfiguration(autoSetupTriggerMapper.fromMaptoStr(List.of(inputConfiguration)));

			autoSetupTriggerManager.saveTriggerUpdate(trigger);
		}
	}

	private void executeEDCTractus(AutoSetupRequest autoSetupRequest, AppActions action, AutoSetupTriggerEntry trigger,
			Map<String, String> inputConfiguration, SelectedTools selectedTool) {

		String label = selectedTool.getLabel();
		selectedTool.setLabel("edc-" + label);

		Map<String, String> edcOutput = edcConnectorWorkFlow.getWorkFlow(autoSetupRequest.getCustomer(), selectedTool,
				action, inputConfiguration, trigger);
		inputConfiguration.putAll(edcOutput);

		edcDeployemnt(autoSetupRequest, trigger, edcOutput);
	}

	private void edcDeployemnt(AutoSetupRequest autoSetupRequest, AutoSetupTriggerEntry trigger,
			Map<String, String> edcOutput) {

		List<Map<String, String>> extractResultMap = extractEDCResultMap(edcOutput);
		String json = autoSetupTriggerMapper.fromMaptoStr(extractResultMap);

		trigger.setAutosetupResult(json);

		trigger.setStatus(TriggerStatusEnum.SUCCESS.name());

		Customer customer = autoSetupRequest.getCustomer();

		String connectivityTestStr = edcOutput.get(CONNECTOR_TEST_RESULT);

		boolean isTestConnectivityTestSuccess = connectivityTestStr != null
				&& connectivityTestStr.contains("consumer and provider");

		String generateEmailTable = generateEmailTable(extractResultMap);
		// Send an email
		Map<String, Object> emailContent = new HashMap<>();
		emailContent.put(ORGNAME, customer.getOrganizationName());
		emailContent.put(CCEMAIL, technicalEmail);
		emailContent.put(TEST_SERVICE_URL, findValueInMap(edcOutput, TEST_SERVICE_URL));
		emailContent.put(CONNECTOR_TEST_RESULT, CONNECTOR_TEST_RESULT);
		emailContent.put(CONTENT, generateEmailTable);

		if (isTestConnectivityTestSuccess) {
			emailContent.put(TOEMAIL, customer.getEmail());
			emailManager.sendEmail(emailContent, "EDC Application Activited Successfully", "edc_success_activate.html");
		} else {
			emailContent.put(TOEMAIL, technicalEmail);
			emailManager.sendEmail(emailContent, "EDC Application Deployed Successfully", SUCCESS_HTML_TEMPLATE);
		}
		log.info(EMAIL_SENT_SUCCESSFULLY);
	}

	private void executeSDEWithEDCTractus(AutoSetupRequest autoSetupRequest, AppActions action,
			AutoSetupTriggerEntry trigger, Map<String, String> inputConfiguration, Customer customer,
			SelectedTools selectedTool) {

		String label = selectedTool.getLabel();
		selectedTool.setLabel("edc-" + label);

		Map<String, String> edcOutput = edcConnectorWorkFlow.getWorkFlow(autoSetupRequest.getCustomer(), selectedTool,
				action, inputConfiguration, trigger);
		inputConfiguration.putAll(edcOutput);

		String json = autoSetupTriggerMapper.fromMaptoStr(extractEDCResultMap(inputConfiguration));
		trigger.setAutosetupResult(json);

		sdeDeployment(autoSetupRequest, action, trigger, inputConfiguration, customer, selectedTool, label);

	}

	private void dtDeployment(Customer customer, AppActions action, AutoSetupTriggerEntry trigger,
			Map<String, String> inputConfiguration, SelectedTools selectedTool) {

		String label = selectedTool.getLabel();
		selectedTool.setLabel("dt-" + label);

		dtAppWorkFlow.getWorkFlow(customer, selectedTool, action, inputConfiguration, trigger);

		List<Map<String, String>> extractDTResultMap = extractDTResultMap(inputConfiguration);
		String generateEmailTable = generateEmailTable(extractDTResultMap);

		String json = autoSetupTriggerMapper.fromMaptoStr(extractDTResultMap);
		trigger.setAutosetupResult(json);

		trigger.setStatus(TriggerStatusEnum.SUCCESS.name());

		// Send an email
		Map<String, Object> emailContent = new HashMap<>();
		emailContent.put(ORGNAME, customer.getOrganizationName());
		emailContent.put(TOEMAIL, customer.getEmail());
		emailContent.put(CCEMAIL, technicalEmail);
		emailContent.put(CONTENT, generateEmailTable);

		emailManager.sendEmail(emailContent, "DT registry Application Activited Successfully",
				"dt_success_template.html");
		log.info(EMAIL_SENT_SUCCESSFULLY);

	}

	private void sdeDeployment(AutoSetupRequest autoSetupRequest, AppActions action, AutoSetupTriggerEntry trigger,
			Map<String, String> inputConfiguration, Customer customer, SelectedTools selectedTool, String label) {

		if (managedDtRegistry) {
			selectedTool.setLabel("dt-" + label);
			dtAppWorkFlow.getWorkFlow(customer, selectedTool, action, inputConfiguration, trigger);

			String json = autoSetupTriggerMapper.fromMaptoStr(extractDependantAppResult(inputConfiguration));
			trigger.setAutosetupResult(json);
		}

		selectedTool.setLabel("sde-" + label);
		Map<String, String> map = sdeWorkFlow.getWorkFlow(autoSetupRequest.getCustomer(), selectedTool, action,
				inputConfiguration, trigger);

		List<Map<String, String>> extractResultMap = extractResultMap(map);
		String generateEmailTable = generateEmailTable(extractResultMap);

		String json = autoSetupTriggerMapper.fromMaptoStr(extractResultMap);
		trigger.setAutosetupResult(json);
		
		String connectivityTestStr = inputConfiguration.get(CONNECTOR_TEST_RESULT);
		boolean isTestConnectivityTestSuccess = connectivityTestStr != null
				&& connectivityTestStr.contains("consumer and provider");

		Map<String, Object> emailContent = new HashMap<>();
		emailContent.put(ORGNAME, customer.getOrganizationName());
		emailContent.put(CCEMAIL, technicalEmail);
		emailContent.put(CONTENT, generateEmailTable);
		
		if (manualUpdate || !isTestConnectivityTestSuccess) {
			emailContent.put(TOEMAIL, technicalEmail);
			emailManager.sendEmail(emailContent, "SDE Application Deployed Successfully", SUCCESS_HTML_TEMPLATE);
			trigger.setStatus(TriggerStatusEnum.MANUAL_UPDATE_PENDING.name());
		} else {
			trigger.setStatus(TriggerStatusEnum.SUCCESS.name());
			// Send an email
			emailContent.put(TOEMAIL, customer.getEmail());
			emailContent.put(TEST_SERVICE_URL, findValueInMap(map, TEST_SERVICE_URL));
			emailContent.put(CONNECTOR_TEST_RESULT, CONNECTOR_TEST_RESULT);

			emailManager.sendEmail(emailContent, "SDE Application Activited Successfully", "success_activate.html");
			// End of email sending code
		}
		log.info(EMAIL_SENT_SUCCESSFULLY);
	}

	@SneakyThrows
	private void generateNotification(Customer customer, String emailSubject, String content, String template) {

		Map<String, Object> emailContent = new HashMap<>();
		emailContent.put(ORGNAME, customer.getOrganizationName());
		emailContent.put(TOEMAIL, mailReplytoAddress);
		emailContent.put(CCEMAIL, technicalEmail);
		emailContent.put(CONTENT, content);

		emailManager.sendEmail(emailContent, emailSubject, template);
		log.info(EMAIL_SENT_SUCCESSFULLY);
	}

	private void processDeleteTrigger(AutoSetupTriggerEntry trigger, Map<String, String> inputConfiguration) {

		if (trigger != null && trigger.getAutosetupRequest() != null) {
			AutoSetupRequest autoSetupRequest = autoSetupRequestMapper.fromStr(trigger.getAutosetupRequest());

			List<AppServiceCatalogAndCustomerMapping> appCatalogListDetails = verifyIsServiceValid(autoSetupRequest);

			// In future if want to support multiple service as installation we can do
			// easily
			for (AppServiceCatalogAndCustomerMapping appCatalogDetails : appCatalogListDetails) {

				if (appCatalogDetails != null) {
					executeInstallTool(trigger, inputConfiguration, appCatalogDetails);
				} else
					log.info("For Packages deletion autoSetupRequest.getSelectedTools is null");
			}

		} else
			log.info("For Packages deletion the Autosetup Request is null");
	}

	private void executeInstallTool(AutoSetupTriggerEntry trigger, Map<String, String> inputConfiguration,
			AppServiceCatalogAndCustomerMapping appCatalogDetails) {

		List<SelectedTools> selectedTools = getToolInfo(appCatalogDetails);

		List<Map<String, String>> autosetupResult = autoSetupTriggerMapper
				.fromJsonStrToMap(trigger.getAutosetupResult());

		autosetupResult.forEach(inputConfiguration::putAll);

		for (SelectedTools selectedTool : selectedTools) {

			String label = "";
			switch (selectedTool.getTool()) {

			case SDE_WITH_EDC_TRACTUS:

				label = selectedTool.getLabel();

				selectedTool.setLabel("edc-" + label);
				edcConnectorWorkFlow.deletePackageWorkFlow(selectedTool, inputConfiguration, trigger);

				selectedTool.setLabel("dt-" + label);
				dtAppWorkFlow.deletePackageWorkFlow(selectedTool, inputConfiguration, trigger);

				selectedTool.setLabel("sde-" + label);
				AutoSetupRequest orgRequest = customerDetailsMapper.fromStr(trigger.getAutosetupRequest());
				sdeWorkFlow.deletePackageWorkFlow(selectedTool, inputConfiguration, trigger, orgRequest);

				break;

			case EDC_TRACTUS:

				label = selectedTool.getLabel();
				selectedTool.setLabel("edc-" + label);
				edcConnectorWorkFlow.deletePackageWorkFlow(selectedTool, inputConfiguration, trigger);

				break;

			case DT_REGISTRY:

				label = selectedTool.getLabel();
				selectedTool.setLabel("dt-" + label);
				dtAppWorkFlow.deletePackageWorkFlow(selectedTool, inputConfiguration, trigger);

				break;
			default:
				throw new ServiceException(selectedTool.getTool() + " is not supported for auto setup");
			}
		}

		trigger.setStatus(TriggerStatusEnum.SUCCESS.name());

		autoSetupTriggerManager.saveTriggerUpdate(trigger);

		log.info("All Packages deleted successfully!!!!");
	}

	private List<Map<String, String>> extractResultMap(Map<String, String> outputMap) {

		List<Map<String, String>> processResult = new ArrayList<>();

		Map<String, String> dft = new LinkedHashMap<>();
		dft.put("name", "SDE");
		dft.put(SDE_FRONTEND_URL, outputMap.get(SDE_FRONTEND_URL));
		dft.put(SDE_BACKEND_URL, outputMap.get(SDE_BACKEND_URL));
		dft.put("storage.media.bucket", findValueInMap(outputMap, "storage.media.bucket"));
		dft.put("storage.media.endpoint", findValueInMap(outputMap, "storage.media.endpoint"));
		dft.put("storage.media.accessKey", findValueInMap(outputMap, "storage.media.accessKey"));
		dft.put("storage.media.secretKey", findValueInMap(outputMap, "storage.media.secretKey"));

		processResult.add(dft);

		processResult.addAll(extractDependantAppResult(outputMap));

		return processResult;
	}

	private List<Map<String, String>> extractDependantAppResult(Map<String, String> outputMap) {

		List<Map<String, String>> processResult = new ArrayList<>();

		//commentting this beause of dt is get localy managed
		if (managedDTRegistryLocal) {
			Map<String, String> dt = extractDTResultMap(outputMap).get(0);
			processResult.add(dt);
		}

		Map<String, String> edc = extractEDCResultMap(outputMap).get(0);
		processResult.add(edc);

		return processResult;
	}

	private List<Map<String, String>> extractEDCResultMap(Map<String, String> outputMap) {

		List<Map<String, String>> processResult = new ArrayList<>();

		Map<String, String> edc = new LinkedHashMap<>();
		edc.put("name", "EDC");
		edc.put("controlPlaneEndpoint", outputMap.get("controlPlaneEndpoint"));
		edc.put("controlPlaneDataEndpoint", outputMap.get("controlPlaneDataEndpoint"));
		edc.put("dataPlanePublicEndpoint", outputMap.get("dataPlanePublicEndpoint"));
		edc.put("edcApiKey", outputMap.get("edcApiKey"));
		edc.put("edcApiKeyValue", outputMap.get("edcApiKeyValue"));
		edc.put("connectorId", findValueInMap(outputMap, "connectorId"));
		processResult.add(edc);

		return processResult;
	}

	private String findValueInMap(Map<String, String> outputMap, String key) {
		if (outputMap.get(key) != null) {
			return outputMap.get(key);
		}
		return "";
	}

	private List<Map<String, String>> extractDTResultMap(Map<String, String> outputMap) {

		List<Map<String, String>> processResult = new ArrayList<>();

		Map<String, String> dt = new LinkedHashMap<>();
		dt.put("name", "DT");
		dt.put("dtregistryUrlWithURI", outputMap.get("dtregistryUrlWithURI"));
		dt.put("idpClientId", outputMap.get("idpClientId"));
		processResult.add(dt);

		return processResult;
	}

	public String generateEmailTable(List<Map<String, String>> content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		content.forEach(element -> {
			sb.append("<tr><td colspan=\"2\"><b>" + element.get("name") + "</b></td></tr>");
			element.entrySet().forEach(entry -> {
				if (!"name".equals(entry.getKey()))
					sb.append("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>");
			});
		});
		sb.append("</table>");
		return sb.toString();
	}

	public boolean checkNamespaceisExist(String targetNamespace) {

		String namespacesResult = kubeAppManageProxy.checkNamespace(targetCluster, targetNamespace);
		return namespacesResult != null && namespacesResult.contains("true");
	}
}
