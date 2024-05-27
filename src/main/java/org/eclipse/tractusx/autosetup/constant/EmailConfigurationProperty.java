/********************************************************************************
* Copyright (c) 2024 T-Systems International GmbH
* Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.autosetup.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class EmailConfigurationProperty {

	@Value("${mail.smtp.host}")
	private String host;

	@Value("${mail.smtp.port}")
	private String port;

	@Value("${mail.from.address}")
	private String fromAddress;

	@Value("${mail.smtp.starttls.enable}")
	private Boolean startTlsEnable;

	@Value("${mail.smtp.username}")
	private String username;

	@Value("${mail.smtp.password}")
	private String password;

	@Value("${mail.smtp.auth}")
	private Boolean auth;
	
	@Value("${mail.replyto.address}")
	private String replytoAddress;

}
