FROM sbtscala/scala-sbt:eclipse-temurin-alpine-22_36_1.9.9_2.13.13

WORKDIR /app

COPY target/scala-2.13/restful-API-assembly-1.0.jar /app

EXPOSE 4000

CMD scala /app/restful-API-assembly-1.0.jar