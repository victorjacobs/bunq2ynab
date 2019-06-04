FROM gradle:jdk11 AS builder

COPY . /src
WORKDIR /src

RUN useradd -u 10001 bunq2ynab
RUN gradle shadowJar


FROM openjdk:11-slim

COPY --from=builder /src/build/libs/bunq2ynab-1.0-SNAPSHOT-all.jar /app/bunq2ynab.jar
COPY --from=builder /etc/passwd /etc/passwd

USER bunq2ynab
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "bunq2ynab.jar"]
