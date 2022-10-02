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

package net.catenax.autosetup.config;

import java.util.Arrays;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;



@Configuration
public class OpenApiConfig {

	@Value("${keycloak.auth-server-url}")
	private String autURL;
	
	@Value("${keycloak.realm}")
	private String realm;
	
	
	@Bean
	public GroupedOpenApi externalOpenApi() {
		String[] paths = {"/internal/**"};
		return GroupedOpenApi.builder().group("autosetup").pathsToExclude(paths)
				.build();
	}
	
	@Bean
	public GroupedOpenApi internalOpenApi() {
		String[] paths = {"/internal/**"};
		return GroupedOpenApi.builder().group("internal").pathsToMatch(paths)
				.build();
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components()
								.addSecuritySchemes("Authentication", new SecurityScheme()
										.type(SecurityScheme.Type.OAUTH2)
										.bearerFormat("jwt")
							            .in(SecurityScheme.In.HEADER)
							            .name("Authorization")
										.flows(new OAuthFlows()
												.authorizationCode(
														new OAuthFlow()
														.authorizationUrl(autURL+"/realms/"+realm+"/protocol/openid-connect/auth")
														.tokenUrl(autURL+"/realms/"+realm+"/protocol/openid-connect/token")
													)
												 )
										)
								)
				.security(Arrays.asList(new SecurityRequirement().addList("Authentication")))
				.info(new Info()
						.title("Auto Setup API information")
						.description("This Service handles all auto setup related operations")
						.version("1.0"));
	}
}
