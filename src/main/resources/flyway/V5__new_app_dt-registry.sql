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

INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('DT_REGISTRY', 'default', 'kubeapps', '{
    "postgresql": {
	   "auth": {
        "password":"$\{rgdbpass\}",
        "username":"$\{rgusername\}",
        "database":"$\{rgdatabase\}"
      },
	  "primary":
	    {
		 "persistence":{
		      "size" :"1Gi"
		  }
		},
		"persistence": {
		    "size" :"1Gi"
		}
	},
    "registry": {
        "host": "$\{dnsName\}",
		"idpClientId" : "$\{idpClientId\}",
		"idpIssuerUri": "$\{idpIssuerUri\}",
		"tenantId" : "$\{tenantId\}",
		"image" :"sldt-digital-twin-registry-image",
        "ingress": {
                "enabled": true,
                "hostname": "$\{dnsName\}",
                "annotations": {
				      "cert-manager.io/cluster-issuer": letsencrypt-prod,
				      "nginx.ingress.kubernetes.io/cors-allow-credentials": "true",
				      "nginx.ingress.kubernetes.io/enable-cors": "true",
				      "nginx.ingress.kubernetes.io/rewrite-target": /$2,
				      "nginx.ingress.kubernetes.io/use-regex": "true",
				      "nginx.ingress.kubernetes.io/x-forwarded-prefix": $\{dtregistryUrlPrefix\}
				},
				"urlPrefix": $\{dtregistryUrlPrefix\},
                "className": "nginx",
                "tls": {
                    "enabled": true,
                    "secretName": "dtregistry"
                },
                "certManager": {
                    "clusterIssuer": "letsencrypt-prod"
                }
            }
    }
}', NULL, 'tx-all-repo/registry', '0.3.0', 'helm.packages', 'v1alpha1', '$\{yamlValues\}', 'JSON');

INSERT INTO app_service_catalog_tbl
(canonical_service_id, ct_name, service_tools, workflow)
VALUES('DT-REGISTRY', 'DT-REGISTRY', '[{"tool": "DT_REGISTRY","label": "dt"}]', 'DT_REGISTRY');

update app_tbl set expected_input_data= replace(replace(expected_input_data,'\{','{'),'\}','}'), required_yaml_configuration=replace(replace(required_yaml_configuration,'\{','{'),'\}','}');
