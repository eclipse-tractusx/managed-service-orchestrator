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

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.autosetup.constant.AppNameConstant;
import org.eclipse.tractusx.autosetup.factory.AppFactory;
import org.eclipse.tractusx.autosetup.kubeapp.model.CreateInstalledPackageRequest;
import org.eclipse.tractusx.autosetup.kubeapps.proxy.KubeAppManageProxy;
import org.eclipse.tractusx.autosetup.mapper.CreatePackageMapper;
import org.eclipse.tractusx.autosetup.wrapper.model.CreatePackageRequest;

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

		String appName = app.name().replace("_", "");

		String updatePackage = kubeAppManageProxy.updatePackage(appWithStandardInfo.getPluginName(),
				appWithStandardInfo.getPluginVersion(), appWithStandardInfo.getTargetCluster(),
				appWithStandardInfo.getTargetNamespace(), packageName + "-" + appName.toLowerCase(),
				updateControlPlane);
		log.info(packageName + "-" + app.name() + " package updated");
		return updatePackage;

	}

	public void deletePackage(AppNameConstant app, String packageName, Map<String, String> inputProperties) {

		log.info(packageName + "-" + app.name() + " package deleting ");
		CreatePackageRequest appWithStandardInfo = appFactory.getAppInputRequestwithrequireDetails(app,
				inputProperties);

		CreateInstalledPackageRequest updateControlPlane = createPackageMapper
				.getUpdatePackageRequest(appWithStandardInfo, app.name(), packageName);

		String appName = app.name().replace("_", "");

		kubeAppManageProxy.deletePackage(appWithStandardInfo.getPluginName(), appWithStandardInfo.getPluginVersion(),
				appWithStandardInfo.getTargetCluster(), appWithStandardInfo.getTargetNamespace(),
				packageName + "-" + appName.toLowerCase(), updateControlPlane);
		log.info(packageName + "-" + app.name() + " package deleted ");

	}

}
