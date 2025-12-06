package com.example.payara.hello.test;

import com.example.payara.hello.test.client.HelloApplicationClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

    @ParameterizedTest
    @CsvSource(value={
            "helloWorld;Hello, World!;","helloUserWorld;Hello, User!;UNPRIVILEGED_USER","helloAdminWorld;Hello, Admin!;ADMINISTRATOR"
    }, delimiter = ';')
    void testHello(String method, String message, String role){
        logger.info("Testing " + method + " returns message: " + message);
        // Query the health endpoint to ensure that the application is deployed
        client.waitForServiceToBeHealthy();
        try {
            // Test that the endpoint produces the expected HTTP response
            assertEquals(message, client.getClass().getDeclaredMethod(method).invoke(client));
        } catch (Exception e) {
            logger.info(parseCommandOutput("docker logs test-classes-payara-deployment-test-1", ""));
            fail("Client Exception: ", e);
        }
        var errorOutput = parseCommandOutput("docker logs test-classes-payara-deployment-test-1", "SEVERE");
        if (!errorOutput.isEmpty()) {
            // Test that no SEVERE exception is present in Payara server logs
            logger.info(parseCommandOutput("docker logs test-classes-payara-deployment-test-1", ""));
            fail("SEVERE exception found in server logs");
        } else if (role != null && !role.isEmpty()){
            // Test that role check is present in Payara server logs
            var roleMsg = "Checking if servlet com.example.payara.hello.HelloApplication with principal PASS_ALL_USER has role "
                            + role + " isGranted: true";
            var roleCheckOutput = parseCommandOutput("docker logs test-classes-payara-deployment-test-1", roleMsg);
            if (roleCheckOutput.isEmpty()) {
                logger.info(parseCommandOutput("docker logs test-classes-payara-deployment-test-1", ""));
                fail("Role " + role + " check not found in server logs");
            }
        }
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