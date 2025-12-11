package com.example.payara.hello;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/hello")
@ApplicationScoped
public class HelloResource {

    MessageResource messageResource;

    public HelloResource() {
        // NOOP (EJB proxyable shenanigans)
    }

    @Inject
    public HelloResource(MessageResource messageResource) {
        this.messageResource = messageResource;
    }

    /**
     * @return The account resource.
     */
    @Path("message")
    public MessageResource getMessageResource() {
        return messageResource;
    }

}
