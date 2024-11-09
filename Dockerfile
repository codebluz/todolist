# Etapa de build
FROM ubuntu:latest AS build

# Atualize e instale Java e Maven
RUN apt-get update && apt-get install -y openjdk-17-jdk maven

# Defina o diretório de trabalho para o projeto
WORKDIR /app

# Copie o arquivo pom.xml e as dependências antes do código-fonte para otimizar o cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie o código-fonte do projeto
COPY . .

# Compile o projeto
RUN mvn clean install

# Etapa final
FROM openjdk:17-jdk-slim

# Exponha a porta da aplicação
EXPOSE 8080

# Copie o JAR gerado para a imagem final
COPY --from=build /app/target/todolist-0.0.1-SNAPSHOT.jar app.jar

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]

