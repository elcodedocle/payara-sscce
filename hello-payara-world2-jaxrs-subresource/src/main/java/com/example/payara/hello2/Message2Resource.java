package com.example.payara.hello2;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@ApplicationScoped
public class Message2Resource {

    public Message2Resource() {
        // NOOP (EJB proxyable shenanigans)
    }

    @GET
    @Produces("text/plain")
    public String helloUnprotected() {
        return "Hello2, World!";
    }

    @GET
    @Produces("text/plain")
    @RolesAllowed("UNPRIVILEGED_USER")
    @Path("user")
    public String helloRestrictedToUnprivilegedUser() {
        return "Hello2, User!";
    }

    @GET
    @Produces("text/plain")
    @RolesAllowed("ADMINISTRATOR")
    @Path("admin")
    public String helloRestrictedToAdministrator() {
        return "Hello2, Admin!";
    }
}