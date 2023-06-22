/********************************************************************************
 * Copyright (c) 2023 T-Systems International GmbH
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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


DELETE FROM app_tbl WHERE app_name='DFT_BACKEND';

DELETE FROM app_tbl WHERE app_name='EDC_CONTROLPLANE';

DELETE FROM app_tbl WHERE app_name='DFT_FRONTEND';

DELETE FROM app_tbl WHERE app_name='POSTGRES_DB';

DELETE FROM app_service_catalog_tbl;

INSERT INTO app_service_catalog_tbl
(canonical_service_id, ct_name, service_tools, workflow)
VALUES('SDE-WITH-EDC-TX', 'SDE-WITH-EDC-TX', '[{"tool": "SDE_WITH_EDC_TRACTUS","label": "sdeedctx"}]', 'EDC_TX_SDE');

INSERT INTO app_service_catalog_tbl
(canonical_service_id, ct_name, service_tools, workflow)
VALUES('EDC-TX', 'EDC-TX', '[{"tool": "EDC_TRACTUS","label": "edctx"}]', 'EDC_TX');

INSERT INTO app_service_catalog_tbl
(canonical_service_id, ct_name, service_tools, workflow)
VALUES('DT-REGISTRY', 'DT-REGISTRY', '[{"tool": "DT_REGISTRY","label": "dt"}]', 'DT_REGISTRY');

update app_tbl set package_version='0.3.5' where app_name='DT_REGISTRY';

INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('SDE', 'default', 'kubeapps', '{
   "sdepostgresql":{
      "enabled":true,
      "primary":{
         "persistence":{
            "size":"1Gi"
         }
      },
      "persistence":{
         "size":"1Gi"
      },
      "auth":{
         "secretKeys":{
            "password":"$\{postgresPassword\}"
         },
         "username":"$\{username\}",
         "database":"$\{database\}"
      }
   },
   "backend": {
		   "ingresses":[
		      {
		         "enabled":true,
		         "hostname":"backend.$\{dnsName\}",
		         "annotations":{
		            
		         },
		         "className":"nginx",
		         "endpoints":[
		            "default"
		         ],
		         "tls":{
		            "enabled":true,
		            "secretName":"dftbackend"
		         },
		         "certManager":{
		            "clusterIssuer":"letsencrypt-prod"
		         }
		      }
		   ],
		   "configuration":{
		      "properties": "server.port=8080
		
							spring.main.allow-bean-definition-overriding=true
								
							spring.servlet.multipart.enabled=true
							
							spring.servlet.multipart.file-size-threshold=2KB
							
							spring.servlet.multipart.max-file-size=200MB
							
							spring.servlet.multipart.max-request-size=215MB
							
							server.servlet.context-path=/dftbackend/api
							
							spring.flyway.baseline-on-migrate=true
							
							spring.flyway.locations=classpath:/flyway
							
							file.upload-dir=./temp/
							
							logging.level.org.apache.http=info
							
							logging.level.root=info
							
							spring.datasource.driver-class-name=org.postgresql.Driver
							
							spring.jpa.open-in-view=false
							
							digital-twins.hostname=$\{sde.digital-twins.hostname\}
							
							digital-twins.authentication.url=$\{sde.digital-twins.authentication.url\}
							
							digital-twins.authentication.clientId=$\{digital-twins.authentication.clientId\}
								
							digital-twins.authentication.clientSecret=$\{digital-twins.authentication.clientSecret\}
							
							digital-twins.authentication.grantType=client_credentials
							
							dft.hostname=$\{dftBackEndUrl\}
							
							dft.apiKeyHeader=$\{dftBackEndApiKeyHeader\}
							
							dft.apiKey=$\{dftBackEndApiKey\}
							
							manufacturerId=$\{manufacturerId\}
							
							edc.hostname=$\{controlPlaneEndpoint\}
							
							edc.managementpath=/data/v2
							
							edc.apiKeyHeader=$\{edcApiKey\}
							
							edc.apiKey=$\{edcApiKeyValue\}
							
							edc.consumer.hostname=$\{controlPlaneEndpoint\}
							
							edc.consumer.apikeyheader=$\{edcApiKey\}
							
							edc.consumer.apikey=$\{edcApiKeyValue\}
							
							edc.consumer.managementpath=/data/v2
							
							edc.consumer.protocol.path=/api/v1/dsp
							
							keycloak.clientid=$\{dftbackendkeycloakclientid\}
							
							spring.security.oauth2.resourceserver.jwt.issuer-uri=$\{sde.resourceServerIssuer\}
							
							springdoc.api-docs.path=/api-docs
							
							springdoc.swagger-ui.oauth.client-id=$\{dftbackendkeycloakclientid\}
							
							partner.pool.hostname=$\{sde.partner.pool.hostname\}
							
							portal.backend.hostname=$\{sde.portal.backend.hostname\}
							
							connector.discovery.token-url=$\{sde.connector.discovery.token-url\}
							
							connector.discovery.clientId=$\{sde.connector.discovery.clientId\}
							
							connector.discovery.clientSecret=$\{sde.connector.discovery.clientSecret\}
							
							bpndiscovery.hostname=$\{sde.bpndiscovery.hostname\}
							
							discovery.authentication.url=$\{sde.discovery.authentication.url\}
										
							discovery.clientId=$\{sde.discovery.clientId\}
							
							discovery.clientSecret=$\{sde.discovery.clientSecret\}
							
							discovery.grantType=client_credentials"
			}			
	},
	"frontend": {
		   "ingresses":[
		      {
		         "enabled":true,
		         "hostname":"$\{dnsName\}",
		         "annotations":{
		            "kubernetes.io/tls-acme": "true"
		         },
		         "className":"nginx",
		         "endpoints":[
		            "default"
		         ],
		         "tls":{
		            "enabled":true,
		            "secretName":"dftfrontend"
		         },
		         "certManager":{
		            "clusterIssuer":"letsencrypt-prod"
		         }
		      }
		   ],
		   "configuration":{
		      "properties":"REACT_APP_API_URL=$\{dftBackEndUrl\}

							REACT_APP_KEYCLOAK_URL=$\{sde.keycloak.auth\}
							
							REACT_APP_KEYCLOAK_REALM=$\{sde.keycloak.realm\}
							
							REACT_APP_CLIENT_ID=$\{dftfrontendkeycloakclientid\}
							
							REACT_APP_DEFAULT_COMPANY_BPN=$\{bpnNumber\}
							
							REACT_APP_FILESIZE=268435456"
		   }
   }
}', NULL, 'sde-repo/sde', '2.0.2', 'helm.packages', 'v1alpha1', '$\{yamlValues\}', 'PROPERTY');

update app_tbl set expected_input_data= replace(replace(expected_input_data,'\{','{'),'\}','}'), required_yaml_configuration=replace(replace(required_yaml_configuration,'\{','{'),'\}','}');
