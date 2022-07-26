FROM gradle:7.4.1-jdk17 AS build

COPY --chown=gradle:gradle . /home/gradle/project/
WORKDIR /home/gradle/project/
RUN gradle -P localdevelopment :samples:other:file-transfer-http-to-http:consumer:shadowjar --no-daemon

FROM openjdk:17-slim

WORKDIR /app
COPY --from=build /home/gradle/project/samples/other/file-transfer-http-to-http/consumer/build/libs/consumer.jar /app
COPY --from=build /home/gradle/project/samples/other/file-transfer-http-to-http/local-deployment/consumer-config/dataspaceconnector-vault.properties /app
COPY --from=build /home/gradle/project/samples/other/file-transfer-http-to-http/local-deployment/consumer-config/dataspaceconnector-keystore.jks /app
COPY --from=build /home/gradle/project/samples/other/file-transfer-http-to-http/local-deployment/consumer-config/dataspaceconnector-configuration.properties /app

EXPOSE 9191
EXPOSE 9192
EXPOSE 9292

ENTRYPOINT java \
    -Djava.security.edg=file:/dev/.urandom \
    -Dedc.ids.title="Eclipse Dataspace Connector" \
    -Dedc.ids.description="Eclipse Dataspace Connector with MindSphere HTTP extensions" \
    -Dedc.ids.maintainer="https://example.maintainer.com" \
    -Dedc.ids.curator="https://example.maintainer.com" \
    -Dedc.fs.config=$EDC_FS_CONFIG \
    -Dedc.keystore=$EDC_KEYSTORE \
    -Dedc.vault=$EDC_VAULT \ 
    -Dedc.keystore.password=test123 \
    -jar consumer.jar
