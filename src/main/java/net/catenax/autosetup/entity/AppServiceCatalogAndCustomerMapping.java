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

package net.catenax.autosetup.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(value = AppServiceCatalogAndCustomerMappingId.class)
@Table(name = "app_service_catalog_mapping_tbl")
public class AppServiceCatalogAndCustomerMapping {
	
	@Id
	@Column(name = "customer")
	private String customer;
	
	@Id
	@Column(name = "service_id")
	private String serviceId;
	
	@OneToOne(targetEntity = AppServiceCatalog.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "canonical_service_id", referencedColumnName = "canonical_service_id")
	private AppServiceCatalog serviceCatalog;
	
	@Transient
	private String canonicalId;

}
