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

package org.eclipse.tractusx.autosetup.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AppDetailsRequest {

	@NotBlank(message = "AppName is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9][a-zA-ZÀ-ÿ0-9\\-_]+", message = "AppName should not contains special characters")
	private String appName;

	@NotBlank(message = "ContextCluster is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9][a-zA-ZÀ-ÿ0-9\\-_]+", message = "ContextCluster should not contains special characters")
	private String contextCluster;

	@NotBlank(message = "ContextNamespace is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9][a-zA-ZÀ-ÿ0-9\\-_]+", message = "ContextNamespace should not contains special characters")
	private String contextNamespace;

	@NotBlank(message = "PackageIdentifier is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9][a-zA-ZÀ-ÿ0-9\\-_./]+", message = "PackageIdentifier should not contains special characters")
	private String packageIdentifier;

	@NotBlank(message = "PluginName is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9][a-zA-ZÀ-ÿ0-9\\-_./]+", message = "PluginName should not contains special characters")
	private String pluginName;

	@NotBlank(message = "PluginVersion is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9][a-zA-ZÀ-ÿ0-9\\-_./]+", message = "PluginVersion should not contains special characters")
	private String pluginVersion;

	@NotBlank(message = "PackageVersion is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9][a-zA-ZÀ-ÿ0-9\\-_./]+", message = "PackageVersion should not contains special characters")
	private String packageVersion;

	@NotBlank(message = "ExpectedInputData is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9 \"$\n\t\\{\\},\\-_./:=\\[\\]]+", message = "ExpectedInputData should not contains special characters")
	private String expectedInputData;

	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9][a-zA-ZÀ-ÿ0-9\\-_]+", message = "OutputData should not contains special characters")
	private String outputData;

	@NotBlank(message = "RequiredYamlConfiguration is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9 \"$\n\t\\{\\},\\-_./:=\\[\\]]+", message = "RequiredYamlConfiguration should not contains special characters")
	private String requiredYamlConfiguration;

	@NotBlank(message = "YamlValueFieldType is mandatory")
	@Pattern(regexp = "[a-zA-ZÀ-ÿ0-9 \"$\n\t\\{\\},\\-_./:=\\[\\]]+", message = "YamlValueFieldType should not contains special characters")
	private String yamlValueFieldType;

}
