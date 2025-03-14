package io.camunda.blueberry.client;

import io.camunda.blueberry.config.BlueberryConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ElasticSearchAccess {

    BlueberryConfig blueberryConfig;
    public ElasticSearchAccess(BlueberryConfig blueberryConfig) {
        this.blueberryConfig = blueberryConfig;
    }

    public boolean existRepository(String repositoryName) {
        RestTemplate restTemplate = new RestTemplate();
        String url = blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + repositoryName;

        try {
            HttpHeaders response = restTemplate.headForHeaders(url);
            return true;
        } catch (Exception e) {
            return false; // If an exception occurs (e.g., 404 error), assume the repository doesn't exist
        }
    }
}
