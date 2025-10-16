# Use uma imagem base do OpenJDK 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /geo7

# Instala Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copia os arquivos de configuração do Maven
COPY pom.xml .
COPY src ./src

# Compila a aplicação
RUN mvn clean package -DskipTests

# Expõe a porta da aplicação
EXPOSE 8080

# Define o comando para executar a aplicação
CMD ["java", "-jar", "target/geo7-1.0-SNAPSHOT.jar"]
