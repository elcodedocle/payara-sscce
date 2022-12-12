FROM payara/server-full:5.2022.4-jdk11

# Ports being exposed
EXPOSE 8080
EXPOSE 8181

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
COPY hello.war /opt/payara/hello.war

ENTRYPOINT ["/entrypoint.sh"]
