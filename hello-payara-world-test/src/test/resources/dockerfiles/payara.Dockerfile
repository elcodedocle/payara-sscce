FROM debian:bookworm

# Ports being exposed
EXPOSE 8080
EXPOSE 8181

USER root

RUN apt update && \
    apt -y upgrade && \
    apt -y install wget unzip less openjdk-17-jre-headless

ENV PAYARA_VERSION=${payara.version}

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Set environment variables for payara
ENV PAYARA_HOME=/opt/payara6

COPY payara.zip .
RUN unzip -o payara.zip -d /opt && \
    rm -f payara.zip

# Setup payara
RUN mkdir $PAYARA_HOME/.iddocserver &&\
    useradd -d $PAYARA_HOME payara && \
    chown -R payara:payara /opt

ENV PATH=$PATH:$PAYARA_HOME/bin

# Set payara admin password
ENV PAYARA_PASSWORD=changeme
ENV MEM_MAX_RAM_PERCENTAGE=80
ENV MEM_XSS=1024k
RUN echo "AS_ADMIN_PASSWORD=${PAYARA_PASSWORD}" > /tmp/glassfishpwd
RUN chown -R payara:payara /opt
RUN chown -R payara:payara /tmp/glassfishpwd

# Create a first-run mechanism
RUN touch /tmp/firstrun && chown payara:payara /tmp/firstrun

# Configure entrypoint
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
USER payara
WORKDIR $PAYARA_HOME
COPY domain.xml $PAYARA_HOME/glassfish/domains/domain1/config/
COPY *.jar $PAYARA_HOME/glassfish/domains/domain1/lib/
COPY hello.ear $PAYARA_HOME/hello.ear

ENTRYPOINT ["/entrypoint.sh"]
