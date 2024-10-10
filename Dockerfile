FROM mcr.microsoft.com/openjdk/jdk:11-ubuntu
ARG JAR_FILE=build/libs/*.jar	
COPY ${JAR_FILE} pcshop-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/pcshop-0.0.1-SNAPSHOT.jar"]