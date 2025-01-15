FROM maven:3.8.6-eclipse-temurin-17 AS builder
# Imposta la directory di lavoro
RUN apt-get update 
RUN apt-get install -y git 
RUN git clone https://github.com/ChenDario/chat_application_server.git
EXPOSE 3000
# Set the entry point to run the Java application (adjust for actual application entry)
ENTRYPOINT ["java", "-jar", "target/chat_application_server/classes/com/example"]


