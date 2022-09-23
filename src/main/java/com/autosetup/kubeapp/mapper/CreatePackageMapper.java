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

package com.autosetup.kubeapp.mapper;

import org.mapstruct.Mapper;

import com.autosetup.kubeapp.model.AvailablePackageRef;
import com.autosetup.kubeapp.model.Context;
import com.autosetup.kubeapp.model.CreateInstalledPackageRequest;
import com.autosetup.kubeapp.model.Plugin;
import com.autosetup.kubeapp.model.ReconciliationOptions;
import com.autosetup.kubeapp.model.Version;
import com.autosetup.wrapper.model.CreatePackageRequest;

@Mapper(componentModel = "spring")
public abstract class CreatePackageMapper {

	
	public CreateInstalledPackageRequest getCreatePackageRequest(CreatePackageRequest createPackageRequest,
			String appName, String packageName) {
		appName = appName.replace("_", "");
		
		Context context = Context.builder()
				.cluster(createPackageRequest.getContextCluster())
				.namespace(createPackageRequest.getContextNamespace())
				.build();
		
		Context targetContext = Context.builder()
				.cluster(createPackageRequest.getTargetCluster())
				.namespace(createPackageRequest.getTargetNamespace())
				.build();
		
		
		Plugin plugin=Plugin.builder()
				.name(createPackageRequest.getPluginName())
				.version(createPackageRequest.getPluginVersion())
				.build();
		
		AvailablePackageRef availRef = AvailablePackageRef.builder()
				.context(context)
				.identifier(createPackageRequest.getAvailablePackageIdentifier())
				.plugin(plugin)
				.build();
		
		Version pkgVersionReference = Version.builder().version(createPackageRequest.getAvailablePackageVersion()).build();
		
		ReconciliationOptions reconciliationOptions=
				ReconciliationOptions.builder()
				.interval("0")
				.serviceAccountName(packageName+"-"+appName.toLowerCase())
				.suspend(false).build();
		
		
		CreateInstalledPackageRequest createInstalledPackageRequest = 
				CreateInstalledPackageRequest.builder()
				.availablePackageRef(availRef)
				.name(packageName+"-"+appName.toLowerCase())
				.targetContext(targetContext)
				.pkgVersionReference(pkgVersionReference)
				.values(createPackageRequest.getValues())
				.reconciliationOptions(reconciliationOptions)
				.build();
		
		return createInstalledPackageRequest;
	}
	

	public CreateInstalledPackageRequest getUpdatePackageRequest(CreatePackageRequest createPackageRequest,
			String appName, String packageName) {
		appName = appName.replace("_", "");
		
		Plugin plugin=Plugin.builder()
				.name(createPackageRequest.getPluginName())
				.version(createPackageRequest.getPluginVersion())
				.build();
		
		Context context = Context.builder()
				.cluster(createPackageRequest.getContextCluster())
				.namespace(createPackageRequest.getContextNamespace())
				.build();
		
		AvailablePackageRef availRef = AvailablePackageRef.builder()
				.context(context)
				.plugin(plugin)
				.build();
		
		Version pkgVersionReference = Version.builder()
				.version(createPackageRequest.getAvailablePackageVersion())
				.build();
		
		ReconciliationOptions reconciliationOptions=
				ReconciliationOptions.builder()
				.interval("0")
				.serviceAccountName(packageName+"-"+appName.toLowerCase())
				.suspend(false).build();
		
		CreateInstalledPackageRequest createInstalledPackageRequest = 
				CreateInstalledPackageRequest.builder()
				.availablePackageRef(availRef)
				.pkgVersionReference(pkgVersionReference)
				.values(createPackageRequest.getValues())
				.reconciliationOptions(reconciliationOptions)
				.build();
		
		return createInstalledPackageRequest;
	}
	
	
}
