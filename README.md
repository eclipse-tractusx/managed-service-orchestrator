## Managed Service Orchestrator Service

## Description

This repository is part of the overarching eclipse-tractusx project. It contains the Backend for the Managed Service Orchestrator service.

It is a standalone service which can be self-hosted. 
It is prototype implementation for Service provider.
This service will help service provider to set up DFT/SDE with EDC and EDC as service in service provider environment.


### Software Version

```shell
Application version: 1.5.0
Helm release version: 1.5.0
```

# Container images

This application provides container images for demonstration purposes. The base image used, to build this demo application image is eclipse-temurin:17-jdk-alpine

## Notice for Docker image

DockerHub: [https://hub.docker.com/r/tractusx/managed-service-orchestrator](https://hub.docker.com/r/tractusx/managed-service-orchestrator)  <br />
Eclipse Tractus-X product(s) installed within the image:

__Managed Service Orchestrator__

- GitHub: https://github.com/eclipse-tractusx/managed-service-orchestrator
- Project home: https://projects.eclipse.org/projects/automotive.tractusx
- Dockerfile: https://github.com/eclipse-tractusx/managed-service-orchestrator/blob/main/Dockerfile
- Project license: [Apache License, Version 2.0](https://github.com/eclipse-tractusx/managed-service-orchestrator/blob/main/LICENSE)

**Used base image**

- 17-jdk-alpine(https://hub.docker.com/layers/library/eclipse-temurin/17-jdk-alpine/images/sha256-f4766a483f0754930109771aebccb93c6e7a228b1977cf2e3fd49285270a2eb3?context=explore)
- Official Eclipse Temurin DockerHub page: https://hub.docker.com/_/eclipse-temurin
- Eclipse Temurin Project: https://projects.eclipse.org/projects/adoptium.temurin
- Additional information about the Eclipse Temurin images: https://github.com/docker-library/repo-info/tree/master/repos/eclipse-temurin

As with all Docker images, these likely also contain other software which may be under other licenses 
(such as Bash, etc. from the base distribution, along with any direct or indirect dependencies of the primary software being contained).

As for any pre-built image usage, it is the image user's responsibility to ensure that any use of this image complies with any relevant licenses for all software contained within.

## Updating the `DEPENDENCIES` file

To update the [DEPENDENCIES](./DEPENDENCIES) declarations, run:

```shell
mvn org.eclipse.dash:license-tool-plugin:license-check 
```

### For installation guide:

[INSTALL.md](INSTALL.md)


### How to run

Managed Service Orchestrator is a SpringBoot Java software project managed by Maven.

When running, the project requires a postgresql database to be available to connect to. Per default configuration the application expects postgres to run on localhost on port 5432.

You can find the standard credentials as well as further database configurations int the application.properties file in the resource folder.


### Prerequisites
- JDK18
- Postgres 13.2
- Docker
- kubeapps
- Helm chart for Package installation

### Steps
1. Clone the GitHub Repository - https://github.com/eclipse-tractusx/managed-service-orchestrator
2. Get your instance of postgres running
3. Setup your project environment to JDK 18
4. Start the application from your IDE.

## Database
## Flyway
The scripts are in the folder: resources/flyway.<p>
File naming: <b>Vx__script_name.sql</b>, where x is the version number. <p>
When there is a need to change the last script, it is necessary to create a new script with the changes.

Link to flyway documentation: [Documentation](https://flywaydb.org/documentation/) 

## API authentication
Authentication for the backend is handled via an Keycloak. This can be set in the configuration file.


### EDC
GitHub repository with correct version of the Eclipse DataSpace Connector Project: [repository](https://github.com/catenax-ng/product-edc)

### Licenses
Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)

