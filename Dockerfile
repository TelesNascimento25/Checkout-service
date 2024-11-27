FROM openjdk:23
COPY target/checkout-service-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/wiremock /src/main/resources/wiremock
ENTRYPOINT ["java","-jar","/app.jar"]

