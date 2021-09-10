# Build project
FROM maven:3.8.2-jdk-11-slim AS BUILD
WORKDIR /project
COPY ./src ./src
COPY ./pom.xml ./pom.xml
RUN ["mvn", "compile", "assembly:single"]

# Start the project
FROM adoptopenjdk/openjdk11:debian-slim
WORKDIR /project
COPY ./resources ./resources
COPY --from=BUILD /project/target/server.jar /barcode-webservice.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/barcode-webservice.jar", "--port", "8080"]