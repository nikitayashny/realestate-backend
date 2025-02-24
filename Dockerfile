FROM eclipse-temurin:22-jdk AS builder

WORKDIR /app

COPY target/realestate-backend-0.0.1-SNAPSHOT.jar app.jar

COPY src/main/resources/keystore.jks keystore.jks

ENV JAVA_OPTS="-Djavax.net.ssl.keyStore=/app/keystore.jks -Djavax.net.ssl.keyStorePassword=31151324Nick"

ENTRYPOINT ["java", "-jar", "/app/app.jar"]