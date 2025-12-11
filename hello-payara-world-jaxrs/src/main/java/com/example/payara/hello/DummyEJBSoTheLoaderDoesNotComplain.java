package com.example.payara.hello;

import jakarta.ejb.Singleton;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class DummyEJBSoTheLoaderDoesNotComplain {

    Logger logger = Logger.getLogger(DummyEJBSoTheLoaderDoesNotComplain.class.getName());

    public DummyEJBSoTheLoaderDoesNotComplain() {
        this.logger.log(Level.INFO, "Initialised DummyEJBSoTheLoaderDoesNotComplain");
    }

    public void doNothing() {
        logger.log(Level.INFO, "Called DummyEJBSoTheLoaderDoesNotComplain.doNothing()");
    }
}
