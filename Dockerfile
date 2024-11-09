# Etapa de build usando uma imagem base que já inclui Maven e JDK 17
FROM maven:3.8.4-openjdk-17 AS build

# Defina o diretório de trabalho
WORKDIR /app

# Copie apenas o arquivo pom.xml e baixe as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie o código-fonte do projeto
COPY . .

# Compile o projeto e gere o JAR
RUN mvn clean install -DskipTests

# Etapa final para uma imagem leve
FROM openjdk:17-jdk-slim

# Exponha a porta da aplicação
EXPOSE 8080

# Copie o JAR gerado para a imagem final
COPY --from=build /app/target/todolist-0.0.1-SNAPSHOT.jar app.jar

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]


