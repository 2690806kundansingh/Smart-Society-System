# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY discovery-service/pom.xml discovery-service/
COPY api-gateway/pom.xml api-gateway/
COPY user-service/pom.xml user-service/
COPY complaint-service/pom.xml complaint-service/
COPY notification-service/pom.xml notification-service/
COPY complaint-service/src complaint-service/src
RUN mvn -B clean package -pl complaint-service -am -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget
COPY --from=builder /app/complaint-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
