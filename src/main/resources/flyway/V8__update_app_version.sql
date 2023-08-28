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

update app_tbl set package_version='2.1.0' where app_name='SDE';

update app_tbl set package_version='0.5.1' where app_name='EDC_CONNECTOR';

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