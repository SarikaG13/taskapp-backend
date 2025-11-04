FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app

COPY settings.xml /root/.m2/settings.xml

COPY . /app

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=$PORT -jar target/TaskApp-0.0.1-SNAPSHOT.jar"]