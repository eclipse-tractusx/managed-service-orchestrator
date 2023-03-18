# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## [Unreleased]

## [1.2.1] - 2023-03-18

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
