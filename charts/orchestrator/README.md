# managed-service-orchestrator

![Version: 1.5.5](https://img.shields.io/badge/Version-1.5.5-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 1.5.4](https://img.shields.io/badge/AppVersion-1.5.4-informational?style=flat-square)

This service will help service provider to set up DFT/SDE with EDC and EDC as service in service provider environment.

## Source Code

* <https://github.com/eclipse-tractusx/managed-service-orchestrator>

## Requirements

| Repository | Name | Version |
|------------|------|---------|
| https://charts.bitnami.com/bitnami | postgresql | 12.x.x |

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity.podAntiAffinity.preferredDuringSchedulingIgnoredDuringExecution[0].podAffinityTerm.labelSelector.matchExpressions[0].key | string | `"app.kubernetes.io/name"` |  |
| affinity.podAntiAffinity.preferredDuringSchedulingIgnoredDuringExecution[0].podAffinityTerm.labelSelector.matchExpressions[0].operator | string | `"DoesNotExist"` |  |
| affinity.podAntiAffinity.preferredDuringSchedulingIgnoredDuringExecution[0].podAffinityTerm.topologyKey | string | `"kubernetes.io/hostname"` |  |
| affinity.podAntiAffinity.preferredDuringSchedulingIgnoredDuringExecution[0].weight | int | `100` |  |
| autoscaling.enabled | bool | `false` |  |
| image.pullPolicy | string | `"Always"` | Set the Image Pull Policy |
| image.repository | string | `"tractusx/managed-service-orchestrator"` | Image to use for deploying an application |
| image.tag | string | `""` | Image tage is defined in chart appVersion |
| imagePullSecrets | list | `[]` |  |
| ingress.annotations | object | `{}` | Annotations to add to the ingress |
| ingress.className | string | `"nginx"` | a reference to an Ingress Class resource that contains additional configuration including the name of the controller that should implement the class |
| ingress.enabled | bool | `false` | If you want to enable or disable the ingress |
| ingress.host | string | `""` | Host of the application on which application runs |
| livenessProbe.failureThreshold | int | `3` |  |
| livenessProbe.initialDelaySeconds | int | `60` |  |
| livenessProbe.periodSeconds | int | `10` |  |
| livenessProbe.successThreshold | int | `1` |  |
| livenessProbe.timeoutSeconds | int | `1` |  |
| nodeSelector | object | `{}` |  |
| podAnnotations | object | `{}` |  |
| podSecurityContext.fsGroup | int | `2000` |  |
| portContainer | int | `9999` |  |
| postgresql.auth.database | string | `"testdb"` |  |
| postgresql.auth.existingSecret | string | `""` |  |
| postgresql.auth.password | string | `"default"` |  |
| postgresql.auth.port | int | `5432` |  |
| postgresql.auth.postgresPassword | string | `"default"` |  |
| postgresql.auth.username | string | `"testuser"` |  |
| postgresql.enabled | bool | `true` |  |
| postgresql.fullnameOverride | string | `"postgresql"` |  |
| probe.endpoint | string | `"/api/healthz"` |  |
| properties.connectorTestServiceUrl | string | `"default"` |  |
| properties.connectorregisterUrl | string | `"default"` |  |
| properties.connectorregisterkeycloakclientId | string | `"default"` |  |
| properties.connectorregisterkeycloakclientSecret | string | `"default"` |  |
| properties.connectorregisterkeycloaktokenURI | string | `"default"` |  |
| properties.dapsJksUrl | string | `"default"` |  |
| properties.dapsTokenUrl | string | `"default"` |  |
| properties.dapsUrl | string | `"default"` |  |
| properties.dnsname | string | `"default"` |  |
| properties.dnsnameProtocol | string | `"default"` |  |
| properties.edc_miwUrl | string | `"default"` |  |
| properties.edc_ssi_authorityId | string | `"default"` |  |
| properties.emailpassword | string | `"default"` |  |
| properties.emailuser | string | `"default"` |  |
| properties.keycloakAuthserverUrl | string | `"default"` |  |
| properties.keycloakBearerOnly | string | `"true"` |  |
| properties.keycloakClientid | string | `"default"` |  |
| properties.keycloakRealm | string | `"default"` |  |
| properties.keycloakResource | string | `"default"` |  |
| properties.keycloakSslRequired | string | `"external"` |  |
| properties.keycloakUseResourceRoleMappings | string | `"true"` |  |
| properties.kubeappsToken | string | `"default"` |  |
| properties.kubeappsUrl | string | `"default"` |  |
| properties.mail_from | string | `"default"` |  |
| properties.mail_to | string | `"default"` |  |
| properties.manual_connector_registration | string | `"false"` |  |
| properties.manualupdate | string | `"true"` |  |
| properties.password | string | `"default"` |  |
| properties.portalclientid | string | `"default"` |  |
| properties.portalclientsecret | string | `"default"` |  |
| properties.portalemail | string | `"default"` |  |
| properties.portaltokenurl | string | `"default"` |  |
| properties.portalurl | string | `"default"` |  |
| properties.postgres-password | string | `"default"` |  |
| properties.resourceServerIssuer | string | `"default"` |  |
| properties.sde_bpndiscovery_hostname | string | `"default"` |  |
| properties.sde_connector_discovery_clientId | string | `"default"` |  |
| properties.sde_connector_discovery_clientSecret | string | `"default"` |  |
| properties.sde_connector_discovery_token_url | string | `"default"` |  |
| properties.sde_digital_twins_authentication_url | string | `"default"` |  |
| properties.sde_digital_twins_hostname | string | `"default"` |  |
| properties.sde_discovery_authentication_url | string | `"default"` |  |
| properties.sde_discovery_clientId | string | `"default"` |  |
| properties.sde_discovery_clientSecret | string | `"default"` |  |
| properties.sde_discovery_grantType | string | `"default"` |  |
| properties.sde_dtregistry_url_prefix | string | `"default"` |  |
| properties.sde_dtregistryidp_client_id | string | `"default"` |  |
| properties.sde_dtregistrytenant_id | string | `"default"` |  |
| properties.sde_keycloak_auth | string | `"default"` |  |
| properties.sde_keycloak_realm | string | `"default"` |  |
| properties.sde_keycloak_tokenUrl | string | `"default"` |  |
| properties.sde_partner_pool_clientId | string | `"default"` |  |
| properties.sde_partner_pool_clientSecret | string | `"default"` |  |
| properties.sde_partner_pool_hostname | string | `"default"` |  |
| properties.sde_portal_backend_authentication_url | string | `"default"` |  |
| properties.sde_portal_backend_clientId | string | `"default"` |  |
| properties.sde_portal_backend_clientSecret | string | `"default"` |  |
| properties.sde_portal_backend_hostname | string | `"default"` |  |
| properties.sde_resource_server_issuer | string | `"default"` |  |
| properties.smtp_auth | string | `"true"` |  |
| properties.smtp_host | string | `"default"` |  |
| properties.smtp_port | string | `"default"` |  |
| properties.smtp_tls_enable | string | `"true"` |  |
| properties.targetCluster | string | `"default"` |  |
| properties.targetNamesapce | string | `"default"` |  |
| properties.vaultToken | string | `"default"` |  |
| properties.vaultUrl | string | `"default"` |  |
| readinessProbe.failureThreshold | int | `3` |  |
| readinessProbe.initialDelaySeconds | int | `60` |  |
| readinessProbe.periodSeconds | int | `10` |  |
| readinessProbe.successThreshold | int | `1` |  |
| readinessProbe.timeoutSeconds | int | `1` |  |
| replicaCount | int | `1` | Number of Replicas for pods |
| resources.limits.cpu | string | `"900m"` | set a maximum amount of allows CPU utilization by specifying a limit on the container. |
| resources.limits.memory | string | `"2Gi"` | set a maximum amount of allows memory utilization by specifying a limit on the container. |
| resources.requests.cpu | string | `"400m"` | sets the minimum amount of CPU required for the container |
| resources.requests.memory | string | `"2Gi"` | set a minimum amount of allows memory utilization by specifying a limit on the container. |
| secretRef | string | `"managed-service-orchestrator"` |  |
| securityContext.allowPrivilegeEscalation | bool | `false` | Controls whether a process can gain more privilege |
| securityContext.runAsNonRoot | bool | `true` |  |
| securityContext.runAsUser | int | `1000` |  |
| service.port | int | `9999` | Port details for sevice |
| service.portContainer | int | `9999` | Container Port details for sevice |
| service.type | string | `"ClusterIP"` | Type of service |
| serviceAccount.annotations | object | `{}` | Annotations to add to the service account |
| serviceAccount.create | bool | `true` | Specifies whether a service account should be created |
| serviceAccount.name | string | `""` | The name of the service account to use. If not set and create is true, a name is generated using the fullname template |
| tolerations | list | `[]` |  |

----------------------------------------------
Autogenerated from chart metadata using [helm-docs v1.11.3](https://github.com/norwoodj/helm-docs/releases/v1.11.3)
