FROM maven:3.8.6-eclipse-temurin-17 AS builder
# Imposta la directory di lavoro
RUN apt-get update 
RUN apt-get install -y git 
RUN git clone https://github.com/ChenDario/chat_application_server.git
EXPOSE 3000
ENTRYPOINT javac chat_application_server/src/main/java/com.example/Main.java


