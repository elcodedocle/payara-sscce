package com.example.payara.hello;

import com.example.payara.hello.entity.Hello;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class HelloService {

    @Inject
    HelloStorage helloStorage;

    String id;
    Provider provider = new BouncyCastleProvider();

    @PostConstruct
    public void init() {
        var hello = new Hello();
        hello.setMessage("Hello, World!");
        id = helloStorage.merge(hello).getId();
    }

    public String hello() {
        return helloStorage.read(id).getMessage();
    }

}