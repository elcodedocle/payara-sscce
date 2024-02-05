FROM payara/server-full:${payara.version}-jdk17

# Ports being exposed
EXPOSE 8080
EXPOSE 8181

ENV PAYARA_VERSION=${payara.version}
# Set payara admin password
ENV PAYARA_PASSWORD=changeme
USER root
RUN echo "AS_ADMIN_PASSWORD=${PAYARA_PASSWORD}" > /tmp/glassfishpwd
RUN chown -R payara:payara /opt
RUN chown -R payara:payara /tmp/glassfishpwd

# Create a first-run mechanism
RUN touch /tmp/firstrun && chown payara:payara /tmp/firstrun

# Configure entrypoint
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
USER payara
COPY domain.xml /opt/payara/appserver/glassfish/domains/domain1/config/
COPY *.jar /opt/payara/appserver/glassfish/domains/domain1/lib/
COPY hello.ear /opt/payara/hello.ear

ENTRYPOINT ["/entrypoint.sh"]
