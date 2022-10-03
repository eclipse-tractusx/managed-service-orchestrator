# our base build image
FROM maven:3.8.5-openjdk-18-slim as build

# copy the project files
COPY ./pom.xml /pom.xml

# build all dependencies
RUN mvn dependency:go-offline -B

# copy your other files
COPY ./src ./src

# build for release
RUN mvn package

# our final base image
FROM eclipse-temurin:18.0.1_10-jre

ARG UID=7000
ARG GID=7000

# set deployment directory
WORKDIR /dft

# copy over the built artifact from the maven image
COPY --chown=${UID}:${GID} --from=build target/*.jar ./app.jar

RUN chown ${UID}:${GID} /dft

USER ${UID}:${GID}

# set the startup command to run your binary
CMD ["java", "-jar", "./app.jar"]
