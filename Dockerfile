FROM gradle:jdk12 AS builder
COPY . /src
WORKDIR /src
RUN gradle shadowJar

FROM openjdk:12
COPY --from=builder /src/build/libs/bunq2ynab-1.0-SNAPSHOT-all.jar /app/bunq2ynab.jar
WORKDIR /app
CMD ["java", "-jar", "bunq2ynab.jar"]

