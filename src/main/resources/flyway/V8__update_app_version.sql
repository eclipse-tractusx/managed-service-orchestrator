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

update app_tbl set expected_input_data='{
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
	     "postgresPassword":"$\{postgresPassword\}",
         "password":"$\{postgresPassword\}",
         "username":"$\{username\}",
         "database":"$\{database\}"
      }
   },
   "backend": {
		   "ingresses":[
		      {
		         "enabled":true,
		         "hostname":"$\{dnsName\}",
		         "annotations":{
		            
		         },
		         "className":"nginx",
		         "endpoints":[
		            "default"
		         ],
		         "tls":{
		            "enabled":true,
		            "secretName":"sdebackend"
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
							
							server.servlet.context-path=/backend/api
							
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
							
							dft.hostname=$\{sdeBackEndUrl\}
							
							dft.apiKeyHeader=$\{sdeBackEndApiKeyHeader\}
							
							dft.apiKey=$\{sdeBackEndApiKey\}
							
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
							
							keycloak.clientid=$\{sdebackendkeycloakclientid\}
							
							spring.security.oauth2.resourceserver.jwt.issuer-uri=$\{sde.resourceServerIssuer\}
							
							springdoc.api-docs.path=/api-docs
							
							springdoc.swagger-ui.oauth.client-id=$\{sdebackendkeycloakclientid\}
							
							partner.pool.hostname=$\{sde.partner.pool.hostname\}
							
							partner.pool.authentication.url=$\{sde.partner.pool.authentication.url\}
							
						    partner.pool.clientId=$\{sde.partner.pool.clientId\}
						    
						    partner.pool.clientSecret=$\{sde.partner.pool.clientSecret\}
						    
						    partner.pool.grantType=client_credentials
							
							portal.backend.hostname=$\{sde.portal.backend.hostname\}
							
							portal.backend.authentication.url=$\{sde.portal.backend.authentication.url\}
 							
 							portal.backend.clientId=$\{sde.portal.backend.clientId\}
 							
 							portal.backend.clientSecret=$\{sde.portal.backend.clientSecret\}
 							
 							portal.backend.grantType=client_credentials
							
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
		            "secretName":"sdefrontend"
		         },
		         "certManager":{
		            "clusterIssuer":"letsencrypt-prod"
		         }
		      }
		   ],
		   "configuration":{
		      "properties":"REACT_APP_API_URL=$\{sdeBackEndUrl\}

							REACT_APP_KEYCLOAK_URL=$\{sde.keycloak.auth\}
							
							REACT_APP_KEYCLOAK_REALM=$\{sde.keycloak.realm\}
							
							REACT_APP_CLIENT_ID=$\{sdefrontendkeycloakclientid\}
							
							REACT_APP_DEFAULT_COMPANY_BPN=$\{bpnNumber\}
							
							REACT_APP_FILESIZE=268435456"
		   }
   }
}',  package_version='2.1.0' where app_name='SDE';;


update app_tbl set expected_input_data= '{
    "install": {
        "postgresql": true,
  		"vault": false
  	},
  	"participant" : { 
  		"id": "$\{bpnNumber\}" 
  	},
    "backendService": {
        "httpProxyTokenReceiverUrl": "$\{dftAddress\}"
    },
    "postgresql": {
        "enabled": true,
        "primary":{
          "persistence":{
            "size":"1Gi"
          }
        },
        "persistence":{
         "enabled":true,
         "size":"1Gi"
        },
        "fullnameOverride": "connectorpostgresqlhost",
        "jdbcUrl":"jdbc:postgresql://connectorpostgresqlhost:5432/edc",
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
                "enabled": false,
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
		"service": {
		    "type": "NodePort"
		},
		"securityContext": {
		   "readOnlyRootFilesystem": false
		},
        "ssi" : {
          "miw" :{
             "authorityId" : "$\{authorityId\}",
             "url": "$\{edcMiwUrl\}"
          },
          "oauth": {
             "client" :{
                "id" :"$\{keycloakAuthenticationClientId\}",
                "secretAlias": "client-secret"
             },
             "tokenurl": "$\{keycloakAuthTokenURL\}"
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
}', package_version='0.5.1' where app_name='EDC_CONNECTOR';


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
		"tenantId" : "$\{bpnNumber\}",
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
}', package_version='0.3.21' where app_name='DT_REGISTRY';

update app_tbl set expected_input_data= replace(replace(expected_input_data,'\{','{'),'\}','}'), required_yaml_configuration=replace(replace(required_yaml_configuration,'\{','{'),'\}','}');
