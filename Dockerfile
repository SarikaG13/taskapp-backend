FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . /app

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "java -Dserver.port=$PORT -jar target/TaskApp-0.0.1-SNAPSHOT.jar"]