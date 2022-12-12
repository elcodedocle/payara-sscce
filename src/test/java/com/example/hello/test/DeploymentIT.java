package com.example.hello.test;

import com.example.hello.client.HelloApplicationClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeploymentIT {

    private static final String CONFIG_FILE_NAME = "config.properties";

    Logger logger = Logger.getLogger(this.getClass().getName());
    Properties properties = loadProperties();
    String payaraHost = (String) properties.get("payara.host");
    int payaraPort = Integer.parseInt((String) properties.get("payara.port"));

    private HelloApplicationClient client;

    @BeforeEach
    public void before() {

        client = new HelloApplicationClient("http://"+payaraHost+":"+payaraPort);

    }

    @Test
    void testHello(){
        client.waitForServiceToBeHealthy();
        assertEquals("Hello, World!", client.helloWorld());
        assertEquals("", parseCommandOutput("docker logs test-classes-payara-deployment-test-1", "SEVERE"));
    }

    private Properties loadProperties() {
        var properties = new Properties();
        try (InputStream inputStream =
                     this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new RuntimeException("Sorry, unable to find properties file: " + CONFIG_FILE_NAME);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties from " + CONFIG_FILE_NAME, e);
        }
        properties.forEach((k, v) -> logger.info("Key: "+k+", value: " +v));
        return properties;
    }

    private String parseCommandOutput(final String command, final String contains) {
        try {
            var process = Runtime.getRuntime().exec(command);
            logger.finest("Parsing stdout for " + contains + " log traces...");
            String stdOut = parseInputStream(process.getInputStream(), contains);
            logger.finest("Parsing stderr for " + contains + " log traces...");
            String stdErr = parseInputStream(process.getErrorStream(), contains);
            process.destroy();
            return stdOut + stdErr;
        } catch (Exception e) {
            throw new RuntimeException("Could not load properties from " + e);
        }
    }

    private String parseInputStream(InputStream inputStream, final String contains) throws IOException {
        var stdOut = "";
        try (var br = new BufferedReader(new InputStreamReader(inputStream))) {
            stdOut = br.lines().peek(x -> logger.finest(x)).filter(x -> x.contains(contains)).collect(Collectors.joining("\n"));
        }
        return stdOut;
    }
}