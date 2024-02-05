package com.example.payara.hello.test.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;

public class HelloApplicationClient {
    // this should match the build/finalName in the pom.xml
    private static final String ROOT_CONTEXT = "hello";

    private final String baseUrl;

    private final String apiUrl;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final Client client;

    /**
     * Construct the client with a base url
     *
     * @param baseUrl the base url
     */
    public HelloApplicationClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.apiUrl = baseUrl + "/" + ROOT_CONTEXT + "/api";
        client = ClientBuilder.newClient();
    }

    private void check(Response response) {
        int status = response.getStatus();

        if (status < 200 || status > 299) {
            throw new RuntimeException("Status is not 2xx: " + status);
        }
    }

   public String helloWorld() {
        Response response = client.target(apiUrl + "/hello-world").request().get();

        check(response);

        return response.readEntity(String.class);
    }

    private boolean isHealthy() {
        try {
            String url = baseUrl + "/health";
            logger.info(() -> "Checking health on " + url);
            Response response = client.target(url).request().get();

            return response.getStatus() == Response.Status.OK.getStatusCode();
        } catch (Exception e) {
            return false;
        }
    }

    /** Wait until the payara service is healthy. Max 60 retries are done. */
    public void waitForServiceToBeHealthy() {
        int maxRetries = 60;
        for (int i = 1; i <= maxRetries; i++) {
            if (isHealthy()) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Error in Thread.sleep");
            }
        }
        throw new RuntimeException("Payara did not become healthy for base url: " + baseUrl);
    }
}
