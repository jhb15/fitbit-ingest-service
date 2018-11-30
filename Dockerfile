# Builder image which tests locally
FROM alpine:latest as builder

COPY bin/syslog-ng.conf /etc/syslog/syslog-ng.conf

RUN apk update
RUN apk add maven openjdk8 syslog-ng

# Copy build files
RUN mkdir /app
WORKDIR /app
RUN mkdir src
COPY web web
COPY src src
COPY pom.xml .
COPY domain.xml .

# Maven Stages
RUN mvn clean

# Prepare exploded war for packaging step
RUN echo "Exporting project..." && mvn compile package

# Creates the resulting image
FROM payara/micro:prerelease
RUN wget -nv -O /opt/payara/mariadb-jdbc.jar https://downloads.mariadb.com/Connectors/java/connector-java-2.3.0/mariadb-java-client-2.3.0.jar

USER root

COPY --from=builder /app/target/fitbit-ingest-service-0.1.war /opt/payara/fitbit-ingest-service-0.1.war
COPY --from=builder /app/domain.xml /opt/payara/domain.xml

RUN chmod +x /opt/payara/domain.xml

ENV fitbitClientId 22D8QC
ENV fitbitClientSecret a4b536e0c59f9157f0d198f16e82eceb
ENV fitbitClientCallback http://localhost:8080/fitbit-ingest-service-0.1/

ENV ABERFITNESS_CLI_ID fitbit_ingest_service
ENV ABERFITNESS_CLI_SECRET DavidBeans
ENV ABERFITNESS_CLI_CALLBACK http://localhost:8080/fitbit-ingest-service-0.1/

ENV DB_HOSTNAME 172.17.0.2
ENV DB_PORT 3306
ENV DB_NAME fitbit-ingest
ENV DB_USER james
ENV DB_PASS password

ENTRYPOINT ["java", "-jar", "/opt/payara/payara-micro.jar", "--addJars" ,"/opt/payara/mariadb-jdbc.jar" , "--deploy", "/opt/payara/fitbit-ingest-service-0.1.war", "--domainConfig", "/opt/payara/domain.xml" ]