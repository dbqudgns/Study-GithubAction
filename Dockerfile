FROM openjdk:17-jdk
WORKDIR /app
COPY build/libs/*SNAPSHOT.jar budtree_app.jar
ENTRYPOINT ["java", "-jar", "budtree_app.jar"]