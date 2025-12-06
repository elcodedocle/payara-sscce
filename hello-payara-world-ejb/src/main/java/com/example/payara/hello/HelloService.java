package com.example.payara.hello;

import com.example.payara.hello.entity.Hello;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class HelloService {

    @Inject
    HelloStorage helloStorage;

    String id;
    String userId;
    String adminId;

    @PostConstruct
    public void init() {
        id = getHello("Hello, World!");
        userId = getHello("Hello, User!");
        adminId = getHello("Hello, Admin!");
    }

    private String getHello(String message) {
        var hello = new Hello();
        hello.setMessage(message);
        return helloStorage.merge(hello).getId();
    }

    public String hello() {
        return helloStorage.read(id).getMessage();
    }

    public String helloUser() {
        return helloStorage.read(userId).getMessage();
    }

    public String helloAdmin() {
        return helloStorage.read(adminId).getMessage();
    }

}