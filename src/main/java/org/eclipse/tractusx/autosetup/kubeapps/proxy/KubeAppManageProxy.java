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

package org.eclipse.tractusx.autosetup.kubeapps.proxy;

import org.eclipse.tractusx.autosetup.kubeapps.model.CreateInstalledPackageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "KubeAppManageProxy", url = "${kubeapp.url}", configuration = ProxyConfiguration.class)
public interface KubeAppManageProxy {

	@GetMapping(path = "/apis/core/packages/v1alpha1/installedpackages")
	String getAllInstallPackages();

	@PostMapping(path = "/apis/plugins/resources/v1alpha1/c/{clusterName}/ns")
	String createNamespace(@PathVariable("clusterName") String clusterName,
			@RequestParam("context.namespace") String namespace);

	@GetMapping(path = "/apis/plugins/resources/v1alpha1/c/{clusterName}/ns/{context.namespace}")
	String checkNamespace(@PathVariable("clusterName") String clusterName,
			@PathVariable("context.namespace") String namespace);

	@PostMapping(path = "/apis/core/packages/v1alpha1/installedpackages")
	String createPackage(@RequestBody CreateInstalledPackageRequest createInstalledPackageRequest);

	@PutMapping(path = "/apis/core/packages/v1alpha1/installedpackages/plugin/{packageName}/{packageVersion}/c/{clusterName}/ns/{namespace}/{identifier}")
	String updatePackage(@PathVariable("packageName") String packageName,
			@PathVariable("packageVersion") String packageVersion, @PathVariable("clusterName") String clusterName,
			@PathVariable("namespace") String namespace, @PathVariable("identifier") String identifier,
			@RequestBody CreateInstalledPackageRequest createInstalledPackageRequest);
	
	@GetMapping(path = "/apis/core/packages/v1alpha1/installedpackages/plugin/{packageName}/{packageVersion}/c/{clusterName}/ns/{namespace}/{identifier}")
	String getInstallPackageDetails(@PathVariable("packageName") String packageName,
			@PathVariable("packageVersion") String packageVersion, @PathVariable("clusterName") String clusterName,
			@PathVariable("namespace") String namespace, @PathVariable("identifier") String identifier,
			@RequestBody CreateInstalledPackageRequest createInstalledPackageRequest);


	@DeleteMapping(path = "/apis/core/packages/v1alpha1/installedpackages/plugin/{packageName}/{packageVersion}/c/{clusterName}/ns/{namespace}/{identifier}")
	String deletePackage(@PathVariable("packageName") String packageName,
			@PathVariable("packageVersion") String packageVersion, @PathVariable("clusterName") String clusterName,
			@PathVariable("namespace") String namespace, @PathVariable("identifier") String identifier,
			@RequestBody CreateInstalledPackageRequest createInstalledPackageRequest);

}
