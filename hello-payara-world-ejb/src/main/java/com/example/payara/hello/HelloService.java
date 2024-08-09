package com.example.payara.hello;

import com.example.payara.hello.entity.Hello;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class HelloService {

    @Inject
    HelloStorage helloStorage;

    String id;

    @PostConstruct
    public void init() {
        var hello = new Hello();
        hello.setMessage("Hello, World!");
        id = helloStorage.merge(hello).getId();
    }

    public String hello() {
        return helloStorage.read(id).getMessage();
    }

    public String helloThrowEJBWrappedValidationException() throws ValidationException {
        throw new ValidationException("This should become a 400 after the CustomValidationExceptionMapper maps " +
                "this exception that the EJBExceptionMapper handles after unwrapping the EJBException containing it");
    }

}
