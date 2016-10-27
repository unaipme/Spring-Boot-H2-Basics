FROM java:8

MAINTAINER unai.perez

ADD target/PruebaJPA-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 2425