FROM gradle:7.5-jdk11 as buider

WORKDIR /app

COPY . .

RUN ./gradlew hivemqExtensionZip

FROM hivemq/hivemq-ce:latest

COPY --from=buider /app/build/hivemq-extension /opt/hivemq/extensions/energy-monitor-hivemq-extension

RUN rm -rf /opt/hivemq/extensions/hivemq-allow-all-extension

ENTRYPOINT ["/opt/docker-entrypoint.sh"]
CMD ["/opt/hivemq/bin/run.sh"]
