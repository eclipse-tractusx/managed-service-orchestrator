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

RUN useradd -u 1000 appuser 



ARG UID=2000
ARG GID=3000

# set deployment directory
WORKDIR /autosetup

# copy over the built artifact from the maven image
COPY --chown=2000:3000 --from=build target/*.jar ./app.jar

RUN chown appuser:3000 /autosetup

USER appuser:3000

#RUN chown -R 1000:3000 /autosetup
#USER 1000:3000

# set the startup command to run your binary
CMD ["java", "-jar", "./app.jar"]
