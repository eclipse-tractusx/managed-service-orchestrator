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

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

import com.autosetup.constant.AppNameConstant;
import com.autosetup.exception.ServiceException;
import com.autosetup.factory.AppFactory;
import com.autosetup.kubeapp.mapper.CreatePackageMapper;
import com.autosetup.kubeapp.model.CreateInstalledPackageRequest;
import com.autosetup.proxy.kubeapps.KubeAppManageProxy;
import com.autosetup.wrapper.model.CreatePackageRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KubeAppsPackageManagement {

	private final CreatePackageMapper createPackageMapper;

	private final AppFactory appFactory;

	private final KubeAppManageProxy kubeAppManageProxy;

	public String createPackage(AppNameConstant app, String packageName, Map<String, String> inputProperties) {
		log.info(packageName + "-" + app.name() + " package creating");

		CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app,
				inputProperties);

		String createPackage = kubeAppManageProxy.createPackage(
				createPackageMapper.getCreatePackageRequest(appWithStandardInfo, app.name(), packageName));
		log.info(packageName + "-" + app.name() + " package created");
		return createPackage;

	}

	public String updatePackage(AppNameConstant app, String packageName, Map<String, String> inputProperties) {
		log.info(packageName + "-" + app.name() + " package updating");
		CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app,
				inputProperties);

		CreateInstalledPackageRequest updateControlPlane = createPackageMapper
				.getUpdatePackageRequest(appWithStandardInfo, app.name(), packageName);

		String appName = app.name().replaceAll("_", "");

		String updatePackage = kubeAppManageProxy.updatePackage(appWithStandardInfo.getPluginName(),
				appWithStandardInfo.getPluginVersion(), appWithStandardInfo.getTargetCluster(),
				appWithStandardInfo.getTargetNamespace(), packageName +"-"+ appName.toLowerCase(), updateControlPlane);
		log.info(packageName + "-" + app.name() + " package updated");
		return updatePackage;

	}

	@Retryable(value = {
			ServiceException.class }, maxAttemptsExpression = "${retry.maxAttempts}", backoff = @Backoff(delayExpression = "${retry.backOffDelay}"))
	public void deletePackage(AppNameConstant app, String packageName, Map<String, String> inputProperties) {

		try {
			log.info(packageName + "-" + app.name() + " package deleting ");
			CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app,
					inputProperties);

			CreateInstalledPackageRequest updateControlPlane = createPackageMapper
					.getUpdatePackageRequest(appWithStandardInfo, app.name(), packageName);

			String appName = app.name().replaceAll("_", "");

			kubeAppManageProxy.deletePackage(appWithStandardInfo.getPluginName(),
					appWithStandardInfo.getPluginVersion(), appWithStandardInfo.getTargetCluster(),
					appWithStandardInfo.getTargetNamespace(), packageName +"-"+ appName.toLowerCase(), updateControlPlane);
			log.info(packageName + "-" + app.name() + " package deleted ");
		} catch (Exception e) {

			log.error("DeletePackage failed retry attempt: : {}, Error: {}",
					RetrySynchronizationManager.getContext().getRetryCount() + 1, e.getMessage());

			if (!e.getMessage().contains("404"))
				throw new ServiceException(
						"Error in " + packageName + "-" + app.name() + " package delete " + e.getMessage());
		}

	}

}
