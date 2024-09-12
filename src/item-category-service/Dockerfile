FROM mcr.microsoft.com/openjdk/jdk:17-mariner
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ADD src/main/resources/categories.json categories.json
USER app
EXPOSE 8084:8084
ENTRYPOINT ["java","-jar","/app.jar"]