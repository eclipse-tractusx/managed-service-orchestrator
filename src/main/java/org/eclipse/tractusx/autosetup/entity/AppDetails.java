/********************************************************************************
 * Copyright (c) 2022, 2023 T-Systems International GmbH
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.autosetup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_tbl")
public class AppDetails {

	@Id
	@Column(name = "app_name")
	private String appName;
	
	@Column(name = "context_cluster")
	private String contextCluster;
	
	@Column(name = "context_namespace")
	private String contextNamespace;
	
	@Column(name = "package_identifier")
	private String packageIdentifier;
	
	@Column(name = "plugin_name")
	private String pluginName;
	
	@Column(name = "plugin_version")
	private String pluginVersion;
	
	@Column(name = "package_version")
	private String packageVersion;
	
	@Lob 
	@Column(name = "expected_input_data")
	private String expectedInputData;
	
	@Lob 
	@Column(name = "output_data")
	private String outputData;
	
	@Lob 
	@Column(name = "required_yaml_configuration")
	private String requiredYamlConfiguration;
	
	@Lob 
	@Column(name = "yaml_value_field_type")
	private String yamlValueFieldType;
	
}
