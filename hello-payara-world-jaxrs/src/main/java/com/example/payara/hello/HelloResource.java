package com.example.payara.hello;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/hello-world")
public class HelloResource {

    HelloService helloService;

    @Inject
    public HelloResource(HelloService helloService) {
        this.helloService = helloService;
    }

    @GET
    @Produces("text/plain")
    public String helloUnprotected() {
        return helloService.hello();
    }

    @GET
    @Produces("text/plain")
    @RolesAllowed("UNPRIVILEGED_USER")
    @Path("user")
    public String helloRestrictedToUnprivilegedUser() {
        return helloService.helloUser();
    }

    @GET
    @Produces("text/plain")
    @RolesAllowed("ADMINISTRATOR")
    @Path("admin")
    public String helloRestrictedToAdministrator() {
        return helloService.helloAdmin();
    }
}