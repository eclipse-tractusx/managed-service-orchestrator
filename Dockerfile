#################################################################################
# Copyright (c) 2022,2023 T-Systems International GmbH
# Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
#
# See the NOTICE file(s) distributed with this work for additional
# information regarding copyright ownership.
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# SPDX-License-Identifier: Apache-2.0
################################################################################
 
# our base build image
FROM maven:3.8.7-eclipse-temurin-17 AS builder

# copy the project files
COPY ./pom.xml /pom.xml

# build all dependencies
RUN mvn dependency:go-offline -B 

# copy your other files
COPY ./src ./src

# build for release
RUN mvn clean install -Dmaven.test.skip=true 

FROM eclipse-temurin:20.0.2_9-jdk

ENV USER=autosetupuser
ENV UID=1000
ENV GID=1000

RUN addgroup --gid $GID $USER

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "$(pwd)" \
    --ingroup "$USER" \
    --no-create-home \
    --uid "$UID" \
    "$USER"

USER $USERNAME

WORKDIR /autosetup

# copy over the built artifact from the maven image
COPY --from=builder target/*.jar ./app.jar

EXPOSE 9999
# set the startup command to run your binary
CMD ["java", "-jar", "./app.jar"]
