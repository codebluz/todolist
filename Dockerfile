# Etapa de build usando uma imagem base que já inclui Maven e JDK 17
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .

RUN mvn clean install -DskipTests

# Etapa final para uma imagem leve
FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=build /app/target/todolist-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]




