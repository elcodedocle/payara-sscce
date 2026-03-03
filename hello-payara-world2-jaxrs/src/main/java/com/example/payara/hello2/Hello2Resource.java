package com.example.payara.hello2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@Path("/hello2")
@ApplicationScoped
public class Hello2Resource {

    Message2Resource message2Resource;

    public Hello2Resource() {
        // NOOP (EJB proxyable shenanigans)
    }

    @Inject
    public Hello2Resource(Message2Resource message2Resource) {
        this.message2Resource = message2Resource;
    }

    /**
     * @return The account resource.
     */
    @Path("message")
    public Message2Resource getMessageResource() {
        return message2Resource;
    }

}
