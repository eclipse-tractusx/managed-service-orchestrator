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

package net.catenax.autosetup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.catenax.autosetup.entity.AppDetails;
import net.catenax.autosetup.entity.AppServiceCatalog;
import net.catenax.autosetup.entity.AppServiceCatalogAndCustomerMapping;
import net.catenax.autosetup.exception.NoDataFoundException;
import net.catenax.autosetup.mapper.AppDetailsMapper;
import net.catenax.autosetup.model.AppDetailsRequest;
import net.catenax.autosetup.repository.AppRepository;
import net.catenax.autosetup.repository.AppServiceCatalogRepository;
import net.catenax.autosetup.repository.AppServiceCatalogRepositoryMapping;

@Service
public class AppDetailsService {

	@Autowired
	private AppRepository appRepository;

	@Autowired
	private AppServiceCatalogRepository appServiceCatalogRepository;

	@Autowired
	private AppServiceCatalogRepositoryMapping appServiceCatalogRepositoryMapping;

	@Autowired
	private AppDetailsMapper appDetailsMapper;

	public AppDetails createOrUpdateAppInfo(AppDetailsRequest appDetailsRequest) {
		AppDetails appDetails = appDetailsMapper.from(appDetailsRequest);
		return appRepository.save(appDetails);
	}

	public AppDetails getAppDetails(String appName) {

		return appRepository.findById(appName)
				.orElseThrow(() -> new NoDataFoundException("No data found for " + appName));
	}

	public List<AppDetails> getAppDetails() {
		return appRepository.findAll();
	}

	public AppServiceCatalog createCatalogService(AppServiceCatalog appServiceCatalog) {
		return appServiceCatalogRepository.save(appServiceCatalog);
	}

	public AppServiceCatalog getCatalogService(String appServiceCatalogId) {
		return appServiceCatalogRepository.findById(appServiceCatalogId)
				.orElseThrow(() -> new NoDataFoundException("No data found for " + appServiceCatalogId));
	}

	public List<AppServiceCatalog> getAllCatalogService() {
		return appServiceCatalogRepository.findAll();
	}

	public AppServiceCatalogAndCustomerMapping createCatalogServiceMapping(
			AppServiceCatalogAndCustomerMapping appServiceCatalogAndCustomerMapping) {
		appServiceCatalogAndCustomerMapping
				.setServiceCatalog(getCatalogService(appServiceCatalogAndCustomerMapping.getCanonicalId()));
		return appServiceCatalogRepositoryMapping.save(appServiceCatalogAndCustomerMapping);
	}

	public AppServiceCatalogAndCustomerMapping getCatalogServiceMapping(String appServiceId) {
		return appServiceCatalogRepositoryMapping.findTop1ByServiceId(appServiceId);
	}
	
	public List<AppServiceCatalogAndCustomerMapping> findByServiceIds(List<String> appServiceIds) {
		return appServiceCatalogRepositoryMapping.findAllByServiceIdIn(appServiceIds);
	}

	public List<AppServiceCatalogAndCustomerMapping> getAllCatalogServiceMapping() {
		return appServiceCatalogRepositoryMapping.findAll();
	}

}
