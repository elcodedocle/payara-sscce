package com.example.payara.hello;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/hello-world")
public class HelloResource {

    @Inject
    HelloService helloService;

    @GET
    @Produces("text/plain")
    public String hello() {
        return helloService.hello();
    }
}