package com.example.payara.hello;

import jakarta.ejb.Singleton;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class DummyEJB2SoTheLoaderDoesNotComplain {

    Logger logger = Logger.getLogger(DummyEJB2SoTheLoaderDoesNotComplain.class.getName());

    public DummyEJB2SoTheLoaderDoesNotComplain() {
        this.logger.log(Level.INFO, "Initialised DummyEJB2SoTheLoaderDoesNotComplain");
    }

    public void doNothing() {
        logger.log(Level.INFO, "Called DummyEJB2SoTheLoaderDoesNotComplain.doNothing()");
    }
}
