FROM maven:3.9.6 AS build

# config-server
WORKDIR /microservices-project

# parent pom
COPY pom.xml ./pom.xml
# service-discovery pom
COPY service-discovery/pom.xml ./service-discovery/pom.xml
# api-gateway pom
COPY api-gateway/pom.xml ./api-gateway/pom.xml
# user-service pom
COPY user-service/pom.xml ./user-service/pom.xml
# notification-service pom
COPY notification-service/pom.xml ./notification-service/pom.xml
# config-server pom and src
COPY config-server/pom.xml ./config-server/pom.xml
COPY config-server/src ./config-server/src

WORKDIR /microservices-project
RUN mvn clean package -DskipTests

FROM openjdk:18.0.2.1-jdk

COPY wait-for-services.sh /wait-for-services.sh
RUN chmod +x /wait-for-services.sh

WORKDIR /microservices-project
COPY --from=build /microservices-project/config-server/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
