# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## [Unreleased]

### Changed
- Moved helm charts from `helm/` to `charts`

## [1.0.0] - 2022-10-21
Added helm release, versioning & tagging

## [1.1.0] - 2023-01-25
Release 3

## [1.1.2] - 2023-02-01
Release 3 with Hot Fix

## [1.1.3] - 2023-02-01
Release 3 with Hot Fix


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
