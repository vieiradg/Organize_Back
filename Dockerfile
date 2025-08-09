FROM openjdk:17-jdk-slim
WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

COPY pom.xml .

# Copy Java source code
COPY src/main/java ./src/main/java

COPY src/main/resources/application.properties src/main/resources/application.properties

# Copy Flyway migration scripts
COPY src/main/resources/db/migration ./src/main/resources/db/migration

RUN --mount=type=cache,target=/root/.m2/repository mvn clean package -DskipTests -U
RUN chmod +x target/organize-backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "target/organize-backend-0.0.1-SNAPSHOT.jar"]