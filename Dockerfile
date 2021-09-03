
# Build stage 
FROM maven:3-openjdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn clean compile assembly:single -f /home/app/pom.xml 

# Package stage
FROM openjdk:11-jre-slim
RUN apt update && apt install -y libfreetype-dev && rm -rf /var/lib/apt/lists/*
COPY --from=build /home/app/target/server.jar /usr/local/lib/server.jar
COPY config /config

VOLUME /config

EXPOSE 8080
CMD ["java","-jar","/usr/local/lib/server.jar"]

