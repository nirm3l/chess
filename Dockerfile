FROM maven:3.8.6-openjdk-18 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

FROM openjdk:18-jdk-oraclelinux8

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/*.jar /app/app.jar

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
