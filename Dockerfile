FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . /app

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 3030

CMD ["sh", "-c", "java -jar target/TaskApp-0.0.1-SNAPSHOT.jar"]