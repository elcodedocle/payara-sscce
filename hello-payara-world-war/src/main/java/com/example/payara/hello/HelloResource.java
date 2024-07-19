package com.example.payara.hello;

import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
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

    @GET
    @Path("hello-throw-ejb-wrapped-validation-exception")
    @Produces("application/json")
    public String helloThrowWrapped() throws ValidationException {
        return helloService.helloThrowEJBWrappedValidationException();
    }

    @GET
    @Path("hello-throw-not-ejb-wrapped-validation-exception")
    @Produces("application/json")
    public String helloThrowNotWrapped() {
        throw new ValidationException("This is handled correctly by the ValidationExceptionMapper, and 400 is " +
                "returned");
    }
}