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

INSERT INTO app_tbl
(app_name, context_cluster, context_namespace, expected_input_data, output_data, package_identifier, package_version, plugin_name, plugin_version, required_yaml_configuration, yaml_value_field_type)
VALUES('TRACTUS_CONNECTOR', 'default', 'kubeapps', '{
    "backendService": {
        "httpProxyTokenReceiverUrl": "$\{dftAddress\}"
    },
    "daps": {
        "url": "$\{dapsurl}",
        "clientId": "$\{dapsclientid\}",
        "paths": {
            "jwks": "/jwks.json",
            "token": "/token"
        }
    },
    "postgresql": {
        "enabled": true,
        "jdbcUrl":"$\{edcdatabaseurl\}",
        "username":"$\{username\}",
        "password":"$\{appdbpass\}"
    },
    "vault": {
        "hashicorp": {
            "enabled": true,
	        "url": "$\{vaulturl\}",
	        "token": "$\{vaulttoken\}",
	        "timeout": 30,
            "healthCheck": {
                "enabled": true,
                "standbyOk": false
            },
            "paths": {
                "health": "/v1/sys/health",
                "secret": "$\{valuttenantpath\}"
            }
        },
        "secretNames": {
            "dapsPrivateKey": "$\{certificate-private-key\}",
            "dapsPublicKey": "$\{daps-cert\}",
            "transferProxyTokenEncryptionAesKey": "$\{encryptionkeys\}",
            "transferProxyTokenSignerPrivateKey": "$\{certificate-data-plane-private-key\}",
            "transferProxyTokenSignerPublicKey": "$\{certificate-data-plane-public-key\}"
        }
    },
    "controlplane": {
        "endpoints": {
            "control": {
                "path": "/control",
                "port": "8083"
            },
            "data": {
                "authKey": "$\{edcApiKeyValue\}",
                "path": "/data",
                "port": "8081"
            },
            "default": {
                "path": "/api",
                "port": "8080"
            },
            "ids": {
                "path": "/api/v1/ids",
                "port": "8084"
            },
            "metrics": {
                "path": "/metrics",
                "port": "8085"
            },
            "validation": {
                "path": "/validation",
                "port": "8082"
            }
        },
        "ingresses": [
            {
                "enabled": true,
                "hostname": "$\{dnsName\}",
                "annotations": {},
                "className": "nginx",
                "endpoints": [
                    "ids",
                    "data",
                    "control",
                    "default"
                ],
                "tls": {
                    "enabled": true,
                    "secretName": "edctxcontrolplane"
                },
                "certManager": {
                    "clusterIssuer": "letsencrypt-prod"
                }
            }
        ]
    },
    "dataplane": {
        "ingresses": [
            {
                "enabled": true,
                "hostname": "$\{dnsName\}",
                "annotations": {},
                "className": "nginx",
                "endpoints": [
                    "public"
                ],
                "tls": {
                    "enabled": true,
                    "secretName": "edctxdataplane"
                },
                "certManager": {
                    "clusterIssuer": "letsencrypt-prod"
                }
            }
        ]
    }
}', NULL, 'edcrepo/tractusx-connector', '0.3.0', 'helm.packages', 'v1alpha1', '$\{yamlValues\}', 'JSON');

update app_tbl set expected_input_data= replace(replace(expected_input_data,'\{','{'),'\}','}'), required_yaml_configuration=replace(replace(required_yaml_configuration,'\{','{'),'\}','}');
