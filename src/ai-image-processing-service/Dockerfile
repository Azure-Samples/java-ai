FROM mcr.microsoft.com/openjdk/jdk:17-mariner
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
USER app
EXPOSE 8083:8083
ENTRYPOINT ["java","-jar","/app.jar"]