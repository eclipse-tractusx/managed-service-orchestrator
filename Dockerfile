#/********************************************************************************
#* Copyright (c) 2022 T-Systems International GmbH
#* Copyright (c) 2022 Contributors to the CatenaX (ng) GitHub Organisation
#*
#* See the NOTICE file(s) distributed with this work for additional
#* information regarding copyright ownership.
#*
#* This program and the accompanying materials are made available under the
#* terms of the Apache License, Version 2.0 which is available at
#* https://www.apache.org/licenses/LICENSE-2.0.
#*
#* Unless required by applicable law or agreed to in writing, software
#* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#* License for the specific language governing permissions and limitations
#* under the License.
#*
#* SPDX-License-Identifier: Apache-2.0
#********************************************************************************/

#FROM openjdk:19-jdk-alpine3.16
#FROM maven:latest
#FROM maven:3.8.6-eclipse-temurin-19-focal

#RUN apk update && apk add maven && apk add --upgrade maven

FROM maven:3.8.5-openjdk-18-slim as build

# copy the project files
COPY ./pom.xml /pom.xml

RUN apt-get update -y && apt-get install -y nocache

#RUN  && apk add --upgrade openssl

WORKDIR /app

COPY . /app

RUN mvn clean install -Dmaven.test.skip=true 

#WORKDIR target

#RUN mv kubeapps-wrapper-0.0.1.jar orchestrator-service.jar 

COPY --chown=${UID}:${GID} --from=build target/*.jar ./app.jar

RUN chown ${UID}:${GID} /dft

USER ${UID}:${GID}

ENTRYPOINT ["java","-jar","auto-setup-0.0.1.jar"]

EXPOSE 9999
