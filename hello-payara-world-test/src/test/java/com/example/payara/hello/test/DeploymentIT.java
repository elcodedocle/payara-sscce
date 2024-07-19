package com.example.payara.hello.test;

import com.example.payara.hello.test.client.HelloApplicationClient;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeploymentIT {

    private static final String CONFIG_FILE_NAME = "config.properties";

    Logger logger = Logger.getLogger(this.getClass().getName());
    Properties properties = loadProperties();
    String payaraHost = (String) properties.get("payara.host");
    int payaraPort = Integer.parseInt((String) properties.get("payara.port"));
    int teardownDelay = Integer.parseInt((String) properties.get("payara.teardownDelay"));
    static int staticTeardownDelay;

    private HelloApplicationClient client;

    @BeforeEach
    public void before() {

        client = new HelloApplicationClient("http://"+payaraHost+":"+payaraPort);
        staticTeardownDelay = teardownDelay;

    }

    @AfterAll
    public static void afterAll() throws InterruptedException {

        // wait before teardown
        sleep(staticTeardownDelay);

    }

    @Test
    void testHello(){
        client.waitForServiceToBeHealthy();
        assertEquals("Hello, World!", client.helloWorld(), "Hello world endpoint response message must match.");
        assertEquals("", parseCommandOutput("docker logs test-classes-payara-deployment-test-1", "SEVERE"),
                "There should be no SEVERE log traces in the server logs after a hello world endpoint call.");
        logger.info(parseCommandOutput("docker logs test-classes-payara-deployment-test-1", "successfully deployed in"));
    }

    @Test
    void testHelloBadRequestValidationException(){
        client.waitForServiceToBeHealthy();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), client.helloThrowNotWrappedStatus(), "ValidationException" +
                "should be mapped to a bad request response with a 400 HTTP error status response code.");
        assertEquals("", parseCommandOutput("docker logs test-classes-payara-deployment-test-1", "SEVERE"),
                "There should be no SEVERE log traces in the server logs after a ValidationException is mapped to an" +
                        " error response.");
        logger.info(parseCommandOutput("docker logs test-classes-payara-deployment-test-1", "successfully deployed in"));
    }

    @Test
    void testHelloBadRequestEJBExceptionWrappedValidationException(){
        client.waitForServiceToBeHealthy();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), client.helloThrowWrappedStatus(), "EJBException " +
                "wrapped ValidationException should be mapped to a bad request response with a 400 HTTP status " +
                "error response code.");
        assertEquals("", parseCommandOutput("docker logs test-classes-payara-deployment-test-1", "SEVERE"),
                "There should be no SEVERE log traces in the server logs after an " +
                        "EJBException-wrapped ValidationException is mapped to an error response.");
        logger.info(parseCommandOutput("docker logs test-classes-payara-deployment-test-1", "successfully deployed in"));
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