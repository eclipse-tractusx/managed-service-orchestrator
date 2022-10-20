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

package org.eclipse.tractusx.autosetup.factory;

import java.util.Map;

import org.eclipse.tractusx.autosetup.constant.AppNameConstant;
import org.eclipse.tractusx.autosetup.entity.AppDetails;
import org.eclipse.tractusx.autosetup.factory.builder.AppConfigurationBuilder;
import org.eclipse.tractusx.autosetup.repository.AppRepository;
import org.eclipse.tractusx.autosetup.wrapper.model.CreatePackageRequest;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class AppFactory {

	private final AppRepository appRepository;
	private final AppConfigurationBuilder appConfigurationBuilder;

	@SneakyThrows
	public CreatePackageRequest getAppInputRequestwithrequireDetails(AppNameConstant app,
			Map<String, String> inputProperties) {

		AppDetails appDetails = appRepository.findById(app.name()).orElseThrow(() -> new RuntimeException(
				String.format("The app %s is not supported for auto set up", app.name())));

		String targetCluster = inputProperties.get("targetCluster");
		String targetNamespace = inputProperties.get("targetNamespace");

		CreatePackageRequest createPackageRequest = prepareRequestPojo(appDetails, targetCluster, targetNamespace);
		createPackageRequest.setValues(appConfigurationBuilder.buildConfiguration(appDetails, inputProperties));
		return createPackageRequest;
	}

	private CreatePackageRequest prepareRequestPojo(AppDetails appDetails, String targetCluster,
			String targetNamespace) {
		return CreatePackageRequest.builder()
				.contextCluster(appDetails.getContextCluster())
				.contextNamespace(appDetails.getContextNamespace())
				.targetCluster(targetCluster)
				.targetNamespace(targetNamespace)
				.pluginName(appDetails.getPluginName())
				.pluginVersion(appDetails.getPluginVersion())
				.availablePackageIdentifier(appDetails.getPackageIdentifier())
				.availablePackageVersion(appDetails.getPackageVersion()).build();
	}

}
