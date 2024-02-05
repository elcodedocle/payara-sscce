package com.example.payara.hello;

import com.example.payara.hello.entity.Hello;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Startup
@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class HelloStorage {

    @PersistenceContext(unitName = "HelloPersistence")
    EntityManager em;

    public Hello merge(Hello hello){
        return em.merge(hello);
    }

    public Hello read(String id) {
        return em.find(Hello.class, id);
    }

}