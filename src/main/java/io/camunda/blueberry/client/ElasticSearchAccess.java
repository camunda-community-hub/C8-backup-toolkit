package io.camunda.blueberry.client;

import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.ElasticsearchException;
import io.camunda.blueberry.exception.OperationException;
import org.springframework.http.*;
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

    /**
     * @param repositoryName
     * @param containerType           azure, S3...
     * @param containerName           name of the container to use, where the repository will be saved
     * @param basePathInsideContainer inside the container, the path
     * @throws ElasticsearchException exception in case of error
     */
    public void createRepository(String repositoryName,
                                 String containerType,
                                 String containerName,
                                 String basePathInsideContainer) throws ElasticsearchException {
        RestTemplate restTemplate = new RestTemplate();

        String jsonPayload = String.format("""
                {
                  "type": "%s",
                  "settings": {
                    "container": "%s",
                    "base_path": "%s"
                  }
                }
                """, containerType, containerName, basePathInsideContainer);
        String url = blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + repositoryName;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity with payload and headers
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            // Make PUT request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ElasticsearchException(OperationException.BLUEBERRYERRORCODE.ELASTICSEARCH_CLIENT,
                        response.getStatusCode().value(),
                        "Can't create repository",
                        "Can't create repository [" + repositoryName + "] using container name[" + containerName + "] Type[" + containerType + "] path[" + basePathInsideContainer + "] " + response.getBody());
            }

        } catch (Exception e) {
            throw new ElasticsearchException(OperationException.BLUEBERRYERRORCODE.ELASTICSEARCH_CLIENT, 400,
                    "Can't create repository",
                    "Can't create repository [" + repositoryName + "] using container name[" + containerName + "] Type[" + containerType + "] path[" + basePathInsideContainer + "] " + e.getMessage());
        }

    }
}