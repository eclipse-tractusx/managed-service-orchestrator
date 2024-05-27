# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## [Unreleased]
### Fixed
- Dependabot reported security issues fixed.

## [1.5.5] - 2024-05-13
### Changed
- postgresql DB upgrade

### Fixed
- Update base image due to vulnerabilities
- Fixed the CVE-2024-22262 springframework URL Parsing with Host Validation security issue
- Fixed the CVE-2024-22257 spring-security Broken Access Control With Direct Use of AuthenticatedVoter
- Multiple dependencies updated to maintain latest versions

## [1.5.4] - 2024-03-06
### Fixed
- Fixed CVE-2024-22234 and CVE-2024-22243
- Fixed Trivy security issue CVE-2024-1597

### Changed
- Update Spring Boot to version 3.2.3
- Update commons-text, commons-io, commons-compres, bcprov, bcpkix, snappy-java, jakarta.activation-api

## [1.5.3] - 2024-02-19
### Fixed
- Fixed trivy security issues CVE-2023-34053, CVE-2023-46589, CVE-2023-6378


## [1.5.2] - 2023-11-24

### Fixed

- Fix helm repo name in `INSTALL.md` (#89)
- Fix link to temurin repository in "Notice for Docker images" (#90)
- Remove default connection test, that prevented helm test to succeed (#92)
- Fix Chart names in helm test step (#95)


## [1.5.1] - 2023-11-17

[App release 1.5.0](https://github.com/eclipse-tractusx/managed-service-orchestrator/releases/tag/v1.5.1)

### Changed
 - Update DT asset creation for oauth secret information
 - Updated trivy workflow
 - Changed the base image for security issue

## [1.5.0] - 2023-09-04

[App release 1.5.0](https://github.com/eclipse-tractusx/managed-service-orchestrator/releases/tag/v1.5.0)

### Changed
 - Support DDTR 3.2 for external subject id

## [1.4.2] - 2023-08-22

### Changed
 - Image creation on docker hub
 - Image update for Trivy workflow
 - Updated the deployment to take the image from tractusx
 - Name change for the image
 - Updated the product name
 - Updated Dockerfile image to point to the overarching package
 - Updated the Helm lint file
 - CPU/Memory updated in values file
 - Updated documentation
 - Updated Security file

### Fixed
 - Security issue fix

## [1.4.1] - 2023-08-21

### Fixed
 - Corrected image for Trviy workflow
 - Email notification only on successful component connetcivity test
 - Corrected email template

## [1.4.0] - 2023-08-17

### Fixed
 - Security issue fixed

## [1.3.9] - 2023-08-14

### Added
 - Added interface document

## [1.3.8] - 2023-08-04

### Changed
 - Updated the document

## [1.3.7] - 2023-08-01

### Added
 - Added flag to skip portal integration API point

### Changed
 - Improvement in portal failure logs
 - Security issue fix
 - Latest DEPENDENCIES file

## [1.3.6] - 2023-07-17

### Added
 - Refactor technical user use for bpdm and portal backend for SDE

## [1.3.5] - 2023-07-12

### Added
 - Support for tool app/service type in autosetup

## [1.3.4] - 2023-07-11

### Changed
 - Updated the ARC42 document

## [1.3.3] - 2023-07-06

### Changed
 - Support EDC SSI

## [1.3.2] - 2023-06-30

### Added
 - Addition of Helm lint

### Changed
 - Updated code of conduct

## [1.3.1] - 2023-06-27

### Changed
 - Updated SDE to combine frontend & backend helm charts

## [1.3.0] - 2023-06-23

### Fixed
 - Updated Spring boot version

## [1.2.9] - 2023-06-22

### Added
 - Added support for EDC connector 0.4.1
 - Added registry base url in asset for consumer reference
 - Upgrade for DT registry 0.3.2

## [1.2.8] - 2023-05-31

### Fixed
 - Fixed spring core security issue

### Changed
 - Changed name of repository

## [1.2.7] - 2023-05-16

### Added
 - Added header to the files

### Changed
 - Changed default ingress to false

## [1.2.6] - 2023-05-10

### Added
 - Added .tractusx file

### Fixed
 - Veracode security issue fixes
 - Fixed DEPENDENCIES file issue
 - Specified runAsUser for securitycontext of container
 - Specified base image in Readme

## [1.2.5] - 2023-04-21

### Added
 - Added changes related to digital twin registry
 - Validate input request attribute

### Changed
 - Upgrade spring expression to 6.0.8 version
 - Spring boot upgraded to 3.0.5

### Fixed
 - Fixed for cross site scripting
 - Veracode issue fix

## [1.2.4] - 2023-04-20

### Added
 - Support Digital Twin registry in Autosetup

### Changed
 - Arc 42 update for E2E Data exchange test service integration


## [1.2.3] - 2023-04-07

### Fixed
 - Fixed email sent issue for failure use case of connector test
 - Rename tractus-X connector app name because of ingress access issue

## [1.2.2] - 2023-03-30

### Added
 - Connector test service integration for managed connector connectivity test
 - Update email template for connector status

## [1.2.1] - 2023-03-20

### Fixed
 - Sonar code duplication issue fix

## [1.2.0] - 2023-03-14

### Fixed
 - Security issues fixed
 - Snakeyaml jar issue fixed
 - veracode security issue fixed

### Changed
 - Supports EDC connector 0.1.6
 - Changed base image to eclipse-temurin


## [1.1.5] - 2023-03-02

### Added
 - Added AUTHORS.md, INSTALL.md
 - Added sources in Chart.yaml
 - Added LICENSE in charts
 - Added README.md in charts
 - Springboot jar fixes
 - keycloak jar fixes


### Changed
 - Fixed CHANGELOG.md file format
 - Changed README.md


## [1.1.4] - 2023-02-10

### Added
 - Added semantic versioing
 - Update bounce crystel lib version
 - Organisation name with more special character support
 - Hot fix of Multi requesting of Managed SDE
 - update tomcat, spring security version

### Removed
 - Removed tag from values.yaml

## [1.1.0] - 2023-02-01

### Added
 - Addded Daps wrapper service
 - test issue fix and update dft package as deployment
 - Added .helmignore

### Changed
- Moved helm charts from `helm/` to `charts`

## [1.0.1] - 2023-01-25

### Added
 - Added helm release
 - Added versioning
 - Added tagging
 - Move ARC42.md to docs directory

## [1.0.0] - 2022-10-21

### Added
- Enable triggering of Autosetup process for SDE application
- Enable triggering of Autosetup process for EDC application
- Creation of the certificates for DAPS registration
- Registration of EDC connector into Catena-X dataspace
- Registration of the services in CX-Portal
- Autosetup process is based on KubeApps

### Changed
- Integration to Catena-X Portal

### Known knowns
- Cross side scripting (XSS) shall be mitigated (low risk)
