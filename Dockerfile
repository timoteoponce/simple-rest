FROM java:8-jre-alpine

ADD ./target/docker-workshop-1.0-SNAPSHOT.jar /

CMD ["java", "-jar", "/docker-workshop-1.0-SNAPSHOT.jar"]