FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S smartlead && adduser -S smartlead -G smartlead
COPY --from=build /app/target/*.jar app.jar
RUN chown smartlead:smartlead app.jar
USER smartlead
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=default", "-Dspring.flyway.enabled=true", "-jar", "app.jar"]