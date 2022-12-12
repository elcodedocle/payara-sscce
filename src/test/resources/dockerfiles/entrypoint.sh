#!/bin/bash

if [ -f /tmp/firstrun ]; then
    # Setup everything on first run only
    echo "deploy /opt/payara/hello.war" >> "first_run_post_boot_commands"
    echo "set-microprofile-healthcheck-configuration --enabled=true"
    asadmin start-domain -v --postbootcommandfile=first_run_post_boot_commands
    rm /tmp/firstrun
else
    # Start payara
    asadmin start-domain -v
fi
