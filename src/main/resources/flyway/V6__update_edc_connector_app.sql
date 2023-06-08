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

update app_tbl set expected_input_data= '{
    "install": {
  		"daps": false,
        "postgresql": true,
  		"vault": false
  	},
  	"participant" : { 
  		"id": "$\{bpnNumber\}" 
  	},
    "backendService": {
        "httpProxyTokenReceiverUrl": "$\{dftAddress\}"
    },
    "daps": {
        "url": "$\{dapsurl\}",
        "clientId": "$\{dapsclientid\}",
        "paths": {
            "jwks": "/jwks.json",
            "token": "/token"
        }
    },
    "postgresql": {
        "enabled": true,
        "fullnameOverride": "postgresql",
        "jdbcUrl":"jdbc:postgresql://postgresql:5432/edc",
        "username":"$\{username\}",
        "password":"$\{appdbpass\}",
        "database": "edc",
        "auth":{
        	"username":"$\{username\}",
            "password":"$\{appdbpass\}",
            "postgresPassword":"$\{postgresPassword\}"
        }
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
            "management": {
                "authKey": "$\{edcApiKeyValue\}",
                "path": "/data",
                "port": "8081"
            }
        },
        "ingresses": [
            {
                "enabled": true,
                "hostname": "$\{dnsName\}",
                "annotations": {},
                "className": "nginx",
                "endpoints": [
                    "protocol",
                    "management",
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
}', package_version='0.4.1' where app_name='EDC_CONNECTOR';

update app_tbl set expected_input_data= '{
    "enablePostgres": true,
	"enableKeycloak": false,
    "postgresql": {
	   "auth": {
        "password":"$\{rgdbpass\}",
		"postgresPassword":"$\{rgdbpass\}",
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
		"authentication": true,
        "ingress": {
                "enabled": true,
                "hostname": "$\{dnsName\}",
                "annotations": {
				      "cert-manager.io/cluster-issuer": letsencrypt-prod,
				      "nginx.ingress.kubernetes.io/cors-allow-credentials": "true",
				      "nginx.ingress.kubernetes.io/enable-cors": "true",
				      "nginx.ingress.kubernetes.io/rewrite-target": /$2,
				      "nginx.ingress.kubernetes.io/use-regex": "true",
				      "nginx.ingress.kubernetes.io/x-forwarded-prefix": /$\{dtregistryUrlPrefix\}
				},
				"urlPrefix": /$\{dtregistryUrlPrefix\},
                "className": "nginx",
                "tls": true
            }
    }
}', package_version='0.3.2' where app_name='DT-REGISTRY';

update app_tbl set expected_input_data= replace(replace(expected_input_data,'\{','{'),'\}','}'), required_yaml_configuration=replace(replace(required_yaml_configuration,'\{','{'),'\}','}');
