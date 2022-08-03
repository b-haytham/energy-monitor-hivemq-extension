FROM hivemq/hivemq-ce:latest

# COPY ./extensions/engy-security /opt/hivemq/extensions/engy-security
COPY ./build/hivemq-extension /opt/hivemq/extensions/energy-monitor-hivemq-extension

ENTRYPOINT ["/opt/docker-entrypoint.sh"]
CMD ["/opt/hivemq/bin/run.sh"]
