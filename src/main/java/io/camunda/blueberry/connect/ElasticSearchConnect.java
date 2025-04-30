package io.camunda.blueberry.connect;

import com.fasterxml.jackson.databind.JsonNode;
import io.camunda.blueberry.connect.container.Container;
import io.camunda.blueberry.connect.container.ContainerFactory;
import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.ElasticsearchException;
import io.camunda.blueberry.operation.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ElasticSearchConnect {
    Logger logger = LoggerFactory.getLogger(ElasticSearchConnect.class);

    BlueberryConfig blueberryConfig;

    ContainerFactory containerFactory;

    @Autowired
    private RestTemplate restTemplate; // Injected instance
    private ZeebeConnect zeebeConnect;

    public ElasticSearchConnect(BlueberryConfig blueberryConfig, ContainerFactory containerFactory) {
        this.blueberryConfig = blueberryConfig;
        this.containerFactory = containerFactory;
    }

    public OperationResult connection() {
        OperationResult operationResult = new OperationResult();
        operationResult.success = true;
        return operationResult;
    }

    public OperationResult existRepository(String repositoryName) {
        OperationResult operationResult = new OperationResult();
        String url = blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + repositoryName;
        operationResult.command = "PUT " + url;

        try {
            // ResponseEntity<String> response = restTemplate.getForEntity(operationResult.command, String.class);
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            // receive {"operaterepository":{"type":"azure","uuid":"7BlNbbw3TxadAqc6M523cw","settings":{"container":"elasticsearchcontainer","base_path":"operatebackup"}}}
            if (response.getStatusCode() != HttpStatus.OK) {
                operationResult.success = false;
                operationResult.details = "Can't connect to ElasticSearch [" + operationResult.command + "]: Http " + response.getStatusCode().value();
                return operationResult;
            }

            JsonNode jsonNode = response.getBody();
// is a repository is defined?
            String containerType = jsonNode.path("operaterepository").path("type").asText();
            String containerUuid = jsonNode.path("operaterepository").path("uuid").asText();
            operationResult.details = " ContainerType[" + containerType + "] uuid[" + containerUuid + "]";

            operationResult.resultBoolean = true;
            operationResult.success = true;

            return operationResult;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                operationResult.success = false;
                operationResult.details = "Repository [" + repositoryName + "] does not exist";
            } else {
                operationResult.success = false;
                operationResult.details = e.getMessage();
            }
            return operationResult; // If an exception occurs (e.g., 404 error), assume the repository doesn't exist
        } catch (Exception e) {
            operationResult.success = false;
            operationResult.details = e.getMessage();
            return operationResult; // If an exception occurs (e.g., 404 error), assume the repository doesn't exist
        }
    }

    /**
     * Create a repository with the command
     * <p>
     * To delete it: curl -X DELETE "http://localhost:9200/_snapshot/operaterepository"
     *
     * @param repositoryName
     * @param containerType           azure, S3...
     * @param basePathInsideContainer inside the container, the path
     * @throws ElasticsearchException exception in case of error
     */
    public OperationResult createRepository(String repositoryName,
                                            String containerType,
                                            String basePathInsideContainer) {
        OperationResult operationResult = new OperationResult();
        operationResult.command = blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + repositoryName;
        Container container = containerFactory.getContainerFromType(containerType);
        if (container == null) {
            operationResult.success = false;
            operationResult.details = "Can't find container [" + containerType + "]";
            return operationResult;
        }

        String jsonPayload = container.getElasticsearchPayload(basePathInsideContainer);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity with payload and headers
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            // Make PUT request
            ResponseEntity<String> response = restTemplate.exchange(operationResult.command, HttpMethod.PUT, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                operationResult.success = false;
                operationResult.details = "Can't create repository : code["
                        + response.getStatusCode().value() + " RepositoryName[" + repositoryName
                        + "] using container information[" + container.getInformation()
                        + "] Type[" + containerType + "] path[" + basePathInsideContainer + "] " + response.getBody();
                return operationResult;
            }

            operationResult.success = true;
            return operationResult;

        } catch (Exception e) {
            operationResult.success = false;
            operationResult.details = "Can't create repository : RepositoryName[" + repositoryName
                    + "] using container name[" + container.getInformation()
                    + "] Type[" + containerType + "] path[" + basePathInsideContainer + "] : "
                    + e.getMessage();
            return operationResult;
        }

    }
    /**
     * curl -X PUT http://localhost:9200/_snapshot/zeeberecordrepository/12 -H 'Content-Type: application/json'   \
     * -d '{ "indices": "zeebe-record*", "feature_states": ["none"]}'
     *
     * @param backupId
     * @param operationLog
     */
    public void esBackup(Long backupId, OperationLog operationLog) {
        String zeebeEsRepository = blueberryConfig.getZeebeRecordRepository();

        HttpEntity<?> zeebeEsBackupRequest = new HttpEntity<>(Map.of("indices", "zeebe-record*", "feature_states", List.of("none")));
        ResponseEntity<String> zeebeEsBackupResponse = restTemplate.exchange(blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + blueberryConfig.getZeebeRepository() + "/" + backupId, HttpMethod.PUT, zeebeEsBackupRequest, String.class);
        operationLog.info("Start Zeebe ES Backup on [" + zeebeEsRepository + "] response: " + zeebeEsBackupResponse.getStatusCode().value() + " [" + zeebeEsBackupResponse.getBody() + "]");
    }

    // Method to delete all non-system indices in Elasticsearch
    public void deleteAllNonSystemIndices(OperationLog operationLog) {
        try {
            // Step 1: Fetch all the indices from Elasticsearch (excluding system indices)
            String url = blueberryConfig.getElasticsearchUrl() + "/_cat/indices?h=index";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Check if response is empty
            String indices = response.getBody();
            if (indices == null || indices.trim().isEmpty()) {
                operationLog.info("No indices found to delete.");
                return;
            }

            // Step 2: Split the response to get individual index names
            String[] indexArray = indices.split("\n");

            // Step 3: Loop through each index and delete it
            for (String index : indexArray) {
                // Exclude system indices (those starting with a dot)
                if (!index.startsWith(".")) {
                    String deleteUrl = blueberryConfig.getElasticsearchUrl() + "/" + index;
                    restTemplate.delete(deleteUrl);  // Send DELETE request
                    operationLog.info("Deleted index: " + index);
                }
            }

            // Step 4: Check if any non-system indices still exist using the /_cat/indices?v API
            String updatedIndicesUrl = blueberryConfig.getElasticsearchUrl() + "/_cat/indices?v";
            ResponseEntity<String> updatedResponse = restTemplate.getForEntity(updatedIndicesUrl, String.class);
            String updatedIndices = updatedResponse.getBody();

            // Step 5: Check if any non-system indices are still present
            if (updatedIndices != null && !updatedIndices.trim().isEmpty()) {
                String[] updatedIndexArray = updatedIndices.split("\n");
                boolean indicesDeletedSuccessfully = true;

                for (String indexLine : updatedIndexArray) {
                    // Split the line by whitespace to get index names
                    String[] parts = indexLine.split("\\s+");
                    String index = parts[0];  // The index name is in the first column
                    if (!index.startsWith(".")) {
                        indicesDeletedSuccessfully = false;
                        break;  // If any non-system index exists, set the flag to false
                    }
                }

                // Log the success or failure message
                if (indicesDeletedSuccessfully) {
                    operationLog.info("Successfully deleted all non-system indices.");
                } else {
                    operationLog.warning("Some indices could not be deleted.");
                }
            }

        } catch (Exception e) {
            operationLog.error("Error while deleting indices: " + e.getMessage());
        }
    }

    // Method to restore snapshots from repositories based on BACKUP_TIME_ID
    public void restoreSnapshots(long backupTimeId, OperationLog operationLog) {
        try {
            // Define the list of repository names
            String[] repositories = extractRepositoryNames(operationLog);

            // Elasticsearch endpoint
            String elasticsearchUrl = blueberryConfig.getElasticsearchUrl();

            // Loop through each repository to restore the snapshots
            for (String repository : repositories) {
                operationLog.info("Processing repository: " + repository);

                // Step 1: Query Elasticsearch to get all snapshots in the repository
                String snapshotUrl = elasticsearchUrl + "/_snapshot/" + repository + "/_all";
                ResponseEntity<String> response = restTemplate.getForEntity(snapshotUrl, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    String responseBody = response.getBody();

                    // Step 2: Filter snapshots by BACKUP_TIME_ID (using a simple approach with substring matching)
                    String snapshotPattern = "\"snapshot\":\"[^\"]*" + backupTimeId + "[^\"]*\"";
                    String[] snapshots = responseBody.split("\n");

                    for (String snapshotLine : snapshots) {
                        if (snapshotLine.matches(snapshotPattern)) {
                            String snapshotName = snapshotLine.split(":")[1].replaceAll("[\"{}]", "").trim();
                            if (!snapshotName.isEmpty()) {
                                operationLog.info("Found snapshot matching criteria: "+snapshotName+" in repository: "+ repository);

                                // Step 3: Restore the snapshot
                                String restoreUrl = elasticsearchUrl + "/_snapshot/" + repository + "/" + snapshotName + "/_restore?wait_for_completion=true";
                                restTemplate.postForEntity(restoreUrl, null, String.class);
                                operationLog.info("Restore initiated for snapshot: "+snapshotName+" in repository: "+repository);
                            }
                        }
                    }
                } else {
                    operationLog.error("Failed to fetch snapshots for repository: "+ repository);
                }
            }

            operationLog.info("Restore operations for all repositories completed.");
        } catch (Exception e) {
            operationLog.error("Error during snapshot restore operation: "+ e.getMessage());
        }
    }

    // Helper method to fetch repositories dynamically from Elasticsearch and extract repository names
    private String[] extractRepositoryNames(OperationLog operationLog) {
        // Step 1: Fetch the list of repositories dynamically from Elasticsearch
        String snapshotRepositoriesUrl = blueberryConfig.getElasticsearchUrl() + "/_snapshot/_all";
        ResponseEntity<String> response = restTemplate.getForEntity(snapshotRepositoriesUrl, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            operationLog.error("Failed to fetch snapshot repositories from Elasticsearch.");
            return null;
        }

        String responseBody = response.getBody();
        if (responseBody == null || responseBody.trim().isEmpty()) {
            operationLog.info("No repositories found in Elasticsearch.");
            return null;
        }

        // Step 2: Extract repository names from the response
        return responseBody.replaceAll("[{}\"]", "").split(",\\s*");
    }
}
