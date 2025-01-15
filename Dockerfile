FROM maven:3.8.6-eclipse-temurin-17 AS builder
RUN apt-get update && apt-get install -y git 
RUN git clone https://github.com/ChenDario/chat_application_server.git
WORKDIR /chat_application_server
RUN mvn compile
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "target/chat_application_server/classes/com/example"]


