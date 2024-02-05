#!/bin/bash
if [ -f /tmp/firstrun ]; then
    echo "deploy /opt/payara/hello.ear" >> "first_run_post_boot_commands"
    asadmin start-domain -v --postbootcommandfile=first_run_post_boot_commands
    rm /tmp/firstrun
else
    # Start payara
    asadmin start-domain -v
fi
