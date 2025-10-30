FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . /app

RUN ./mvnw clean package -DskipTests

EXPOSE 3030

CMD ["java", "-jar", "target/TaskApp-0.0.1-SNAPSHOT.jar"]