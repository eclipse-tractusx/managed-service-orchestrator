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

CREATE TABLE app_tbl (
  app_name varchar(255) NOT NULL,
  context_cluster varchar(255) DEFAULT NULL,
  context_namespace varchar(255) DEFAULT NULL,
  expected_input_data text,
  output_data text,
  package_identifier varchar(255) DEFAULT NULL,
  package_version varchar(255) DEFAULT NULL,
  plugin_name varchar(255) DEFAULT NULL,
  plugin_version varchar(255) DEFAULT NULL,
  required_yaml_configuration text,
  yaml_value_field_type text,
  PRIMARY KEY (app_name)
);
CREATE INDEX app_tbl_s_idx ON public.app_tbl USING btree (app_name);

INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('DFT_BACKEND', 'default', 'kubeapps', 'server.port=8080

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

edc.hostname=$\{controlPlaneEndpoint\}

edc.apiKeyHeader=$\{edcApiKey\}

edc.apiKey=$\{edcApiKeyValue\}

dft.hostname=$\{dftBackEndUrl\}

dft.apiKeyHeader=$\{dftBackEndApiKeyHeader\}

dft.apiKey=$\{dftBackEndApiKey\}

manufacturerId=$\{manufacturerId\}

edc.consumer.hostname=$\{controlPlaneEndpoint\}

edc.consumer.apikeyheader=$\{edcApiKey\}

edc.consumer.apikey=$\{edcApiKeyValue\}

edc.consumer.datauri=/api/v1/ids/data

keycloak.clientid=$\{dftbackendkeycloakclientid\}

spring.security.oauth2.resourceserver.jwt.issuer-uri=$\{sde.resourceServerIssuer\}

springdoc.api-docs.path=/api-docs

springdoc.swagger-ui.oauth.client-id=$\{dftbackendkeycloakclientid\}

partner.pool.hostname=$\{sde.partner.pool.hostname\}

portal.backend.hostname=$\{sde.portal.backend.hostname\}

connector.discovery.token-url=$\{sde.connector.discovery.token-url\}

connector.discovery.clientId=$\{sde.connector.discovery.clientId\}

connector.discovery.clientSecret=$\{sde.connector.discovery.clientSecret\}', NULL, 'sde-backend/dftbackend', '2.0.0', 'helm.packages', 'v1alpha1', '{"dftpostgresql": {"enabled": true, "primary":{"persistence":{"size" :"1Gi"}},"persistence":{"size" :"1Gi"}, "auth" : {"secretKeys":{"password":"$\{postgresPassword}"\},"username":"$\{username\}","database":"$\{database\}"}},"ingresses":[{"enabled": true, "hostname":"$\{dnsName\}",  "annotations": {}, "className": "nginx", "endpoints":["default"], "tls":{"enabled":true, "secretName":"dftbackend"}, "certManager":{"clusterIssuer":"letsencrypt-prod"}}], "configuration": {"properties": "$\{yamlValues\}"}}', 'PROPERTY');
INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('DFT_FRONTEND', 'default', 'kubeapps', 'REACT_APP_API_URL=$\{dftBackEndUrl\}

REACT_APP_KEYCLOAK_URL=$\{sde.keycloak.auth\}

REACT_APP_KEYCLOAK_REALM=$\{sde.keycloak.realm\}

REACT_APP_CLIENT_ID=$\{dftfrontendkeycloakclientid\}

REACT_APP_DEFAULT_COMPANY_BPN=$\{bpnNumber\}

REACT_APP_FILESIZE=268435456', NULL, 'sde-frontend/dftfrontend', '2.0.0', 'helm.packages', 'v1alpha1', '{"ingresses":[{"enabled": true, "hostname":"$\{dnsName\}",  "annotations": {}, "className": "nginx", "endpoints":["default"], "tls":{"enabled":true, "secretName":"dftfrontend"}, "certManager":{"clusterIssuer":"letsencrypt-prod"}}], "configuration": {"properties": "$\{yamlValues\}"}}', 'PROPERTY');
INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('EDC_CONTROLPLANE', 'default', 'kubeapps', 'edc.receiver.http.endpoint=$\{dftAddress\}
	
    edc.ids.title=Eclipse_Dataspace_Connector
	
    edc.ids.description=Eclipse_Dataspace_Connector
	
    edc.ids.id=urn:connector:edc
	
    edc.ids.security.profile=base
	
    edc.ids.endpoint=$\{controlPlaneEndpoint\}/api/v1/ids
	
    edc.ids.maintainer=$\{controlPlaneEndpoint\}
	
    edc.ids.curator=$\{controlPlaneEndpoint\}
	
    edc.ids.catalog.id=urn:catalog:default
	
    ids.webhook.address=$\{controlPlaneEndpoint\}
	
    edc.api.control.auth.apikey.key=$\{edcApiKey\}
	
    edc.api.auth.key=$\{edcApiKeyValue\}
	
    edc.hostname=$\{dnsName\}
    
	edc.oauth.provider.audience=idsc:IDS_CONNECTORS_ALL
	
    edc.oauth.token.url=$\{dapstokenurl\}
	
    edc.oauth.client.id=$\{dapsclientid\}
	
    edc.oauth.provider.jwks.url=$\{dapsjsksurl\}

    edc.oauth.public.key.alias=$\{daps-cert\}
	
    edc.oauth.private.key.alias=$\{certificate-private-key\}
	
	edc.vault.hashicorp.url=$\{vaulturl\}
	
	edc.vault.hashicorp.token=$\{vaulttoken\}
	
	edc.vault.hashicorp.timeout.seconds=$\{vaulttimeout\}
	
	edc.vault.hashicorp.api.secret.path=$\{valuttenantpath\}
	
	edc.vault.hashicorp.health.check.standby.ok=false
	
    edc.transfer.proxy.endpoint=$\{dataPlanePublicUrl\}
	
    edc.transfer.proxy.token.signer.privatekey.alias=$\{certificate-data-plane-private-key\}
	
    edc.transfer.dataplane.sync.endpoint=$\{dataPlanePublicUrl\}
	
    edc.transfer.dataplane.token.signer.privatekey.alias=$\{certificate-data-plane-private-key\}
	
    edc.transfer.proxy.token.verifier.publickey.alias=$\{certificate-data-plane-public-key\}
	
    edc.public.key.alias=$\{certificate-data-plane-public-key\}
	
    edc.data.encryption.keys.alias=$\{encryptionkeys\}
   
    edc.ids.endpoint.audience=$\{controlPlaneIdsEndpoint\}
	
    edc.datasource.asset.name=asset
	
    edc.datasource.asset.url=$\{edcdatabaseurl\}
	
    edc.datasource.asset.user=$\{username\}
	
    edc.datasource.asset.password=$\{appdbpass\}
	
    edc.datasource.contractdefinition.name=contractdefinition
	
    edc.datasource.contractdefinition.url=$\{edcdatabaseurl\}
	
    edc.datasource.contractdefinition.user=$\{username\}
	
    edc.datasource.contractdefinition.password=$\{appdbpass\}
	
    edc.datasource.contractnegotiation.name=contractnegotiation
	
    edc.datasource.contractnegotiation.url=$\{edcdatabaseurl\}
	
    edc.datasource.contractnegotiation.user=$\{username\}
	
    edc.datasource.contractnegotiation.password=$\{appdbpass\}
	
    edc.datasource.policy.name=policy
	
    edc.datasource.policy.url=$\{edcdatabaseurl\}
	
    edc.datasource.policy.user=$\{username\}
	
    edc.datasource.policy.password=$\{appdbpass\}
	
    edc.datasource.transferprocess.name=transferprocess
	
    edc.datasource.transferprocess.url=$\{edcdatabaseurl\}
	
    edc.datasource.transferprocess.user=$\{username\}
	
    edc.datasource.transferprocess.password=$\{appdbpass\}', NULL, 'orchestrator/edc-controlplane', '0.1.6', 'helm.packages', 'v1alpha1', '{"ingresses":[{"enabled": true, "hostname": "$\{dnsName\}", "annotations": {}, "className": "nginx", "endpoints":["ids", "data", "control", "default"], "tls":{"enabled": true, "secretName":"edccontrolplane"},"certManager":{"clusterIssuer":"letsencrypt-prod"}}], "configuration": {"properties": "$\{yamlValues\}"}}', 'PROPERTY');
INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('EDC_DATAPLANE', 'default', 'kubeapps', 'edc.hostname=$\{dnsName\}
	
edc.vault.hashicorp.url=$\{vaulturl\}

edc.vault.hashicorp.token=$\{vaulttoken\}

edc.vault.hashicorp.timeout.seconds=$\{vaulttimeout\}

edc.dataplane.token.validation.endpoint=$\{controlPlaneValidationEndpoint\}', NULL, 'orchestrator/edc-dataplane', '0.1.6', 'helm.packages', 'v1alpha1', '{"ingresses":[{"enabled": true, "hostname": "$\{dnsName\}", "annotations": {}, "className": "nginx", "endpoints":["public"], "tls":{"enabled": true, "secretName":"edcdataplane"},"certManager":{"clusterIssuer":"letsencrypt-prod"}}], "configuration": {"properties": "$\{yamlValues\}"}}', 'PROPERTY');
INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('POSTGRES_DB', 'default', 'kubeapps', '{"postgresPassword":"$\{postgresPassword\}",
"username":"$\{username\}",
"password":"$\{appdbpass\}",
"database":"$\{database\}"}', NULL, 'bitnami/postgresql', '11.8.1', 'helm.packages', 'v1alpha1', '{"primary":{"persistence":{"size" :"1Gi"}},"persistence":{"size" :"1Gi"}, "global": {"postgresql" : {"auth" :$\{yamlValues\}}}}', 'JSON');


update app_tbl set expected_input_data= replace(replace(expected_input_data,'\{','{'),'\}','}'), required_yaml_configuration=replace(replace(required_yaml_configuration,'\{','{'),'\}','}');
