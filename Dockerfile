FROM gradle:jdk11 AS builder
COPY . /src
WORKDIR /src
RUN gradle shadowJar

FROM openjdk:11-slim
COPY --from=builder /src/build/libs/bunq2ynab-1.0-SNAPSHOT-all.jar /app/bunq2ynab.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "bunq2ynab.jar"]

