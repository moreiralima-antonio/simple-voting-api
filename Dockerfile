FROM openjdk:11-jre-slim

ADD target/*.jar  /app.jar

CMD java -Dspring.data.mongodb.uri=${MONGODB_URI} -jar /app.jar
