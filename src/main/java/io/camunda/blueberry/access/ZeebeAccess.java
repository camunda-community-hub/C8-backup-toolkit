package io.camunda.blueberry.access;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.blueberry.access.toolbox.WebActuator;
import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.OperationException;
import io.camunda.blueberry.operation.OperationLog;
import io.camunda.zeebe.protocol.impl.encoding.BackupStatusResponse;
import io.camunda.zeebe.protocol.management.BackupStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Component
public class ZeebeAccess extends WebActuator {
    private final ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(ZeebeAccess.class);
    private final BlueberryConfig blueberryConfig;
    private final RestTemplate restTemplate;

    public ZeebeAccess(BlueberryConfig blueberryConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate);
        this.blueberryConfig = blueberryConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void connection() {

    }


    /* ******************************************************************** */
    /*                                                                      */
    /*  Resume and pause                                                    */
    /*                                                                      */
    /* ******************************************************************** */

    public void pauseExporting(OperationLog operationLog) {
        ResponseEntity<String> pauseZeebeExporting = restTemplate.postForEntity(blueberryConfig.getZeebeActuatorUrl() + "/actuator/exporting/pause", new HashMap<>(), String.class);
        operationLog.info("Pause Zeebe exporting");
    }

    public void resumeExporting(OperationLog operationLog) {
        ResponseEntity<String> zeebeResumeResponse = restTemplate.postForEntity(blueberryConfig.getZeebeActuatorUrl() + "/actuator/exporting/resume", new HashMap<>(), String.class);
        operationLog.info("Resume Zeebe exporting");
    }

    /* ******************************************************************** */
    /*                                                                      */
    /*  Elastic search section                                              */
    /*                                                                      */
    /* ******************************************************************** */

    /**curl PUT 'http://localhost:9200/_snapshot/zeeberecordrepository' -H 'Content-Type: application/json' \
            -d '{
            "type": "s3",
            "settings": {
        "bucket": "joeymanagementbucket",
                "base_path": "zeeberecordbackup",
                "region": "us-east-2"
    }
    }'**/
    public void ensureZeebeSnapshotExists(OperationLog operationLog) {
        String esUrl = blueberryConfig.getElasticsearchUrl();
        String zeebeRepository = blueberryConfig.getZeebeRepository();
        String snapshotUrl = esUrl + "/_snapshot/_all";  // Snapshot URL

        try {
            ResponseEntity<String> response = restTemplate.exchange(snapshotUrl, HttpMethod.GET, null, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode repositories = new ObjectMapper().readTree(response.getBody());

                JsonNode zeebeRepositoryJsonNode = repositories.get(zeebeRepository);
                if (zeebeRepositoryJsonNode != null) {
                    operationLog.info("Repository " + zeebeRepository + " exists.");
                    // Optionally print repository details
                    operationLog.info("Repository Details: " + zeebeRepositoryJsonNode.toString());
                } else {
                    operationLog.info("Repository '" + zeebeRepository + "' does not exist.");
                    // Only create the snapshot if repository does not exist
                    createSnapshot(operationLog);
                }
            }
        } catch (Exception e) {
            operationLog.error("Error checking repository existence: " + e.getMessage());
        }
    }

    public void createSnapshot(OperationLog operationLog) {
        ZeebeInformation zeebeInformation = getInformation();
        String zeebeEsRepository = blueberryConfig.getZeebeRecordRepository();

        String storageType = blueberryConfig.getContainerType(); // Fetch storage type (e.g., "s3" or "azure")
        String url = blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + blueberryConfig.getZeebeRepository();

        Map<String, Object> settings;
        String type;

        if ("s3".equalsIgnoreCase(storageType)) {
            type = "s3";
            settings = Map.of(
                    "bucket", blueberryConfig.getS3Bucket(),
                    "base_path", blueberryConfig.getS3BasePath(),
                    "region", blueberryConfig.getS3Region()
            );
        } else if ("azure".equalsIgnoreCase(storageType)) {
            type = "azure";
            settings = Map.of(
                    "container", blueberryConfig.getAzureContainerName(),
                    "base_path", blueberryConfig.getZeebeRecordContainerBasePath());
        } else {
            throw new IllegalArgumentException("Unsupported storage type: " + storageType);
        }

        Map<String, Object> requestBody = Map.of(
                "type", type,
                "settings", settings
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);

        operationLog.info("Created Zeebe ES Snapshot Repository on [" + zeebeEsRepository + "] with storage type [" + type + "] response: " +
                response.getStatusCode().value() + " [" + response.getBody() + "]");
    }


    /**
     * curl -X PUT http://localhost:9200/_snapshot/zeeberecordrepository/12 -H 'Content-Type: application/json'   \
     * -d '{ "indices": "zeebe-record*", "feature_states": ["none"]}'
     *
     * @param backupId
     * @param operationLog
     */
    public void esBackup(Long backupId, OperationLog operationLog) {
        ZeebeInformation zeebeInformation = getInformation();
        String zeebeEsRepository = blueberryConfig.getZeebeRecordRepository();

        HttpEntity<?> zeebeEsBackupRequest = new HttpEntity<>(Map.of("indices", "zeebe-record*", "feature_states", List.of("none")));
        ResponseEntity<String> zeebeEsBackupResponse = restTemplate.exchange(blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + blueberryConfig.getZeebeRepository() + "/" + backupId, HttpMethod.PUT, zeebeEsBackupRequest, String.class);
        operationLog.info("Start Zeebe ES Backup on [" + zeebeEsRepository + "] response: " + zeebeEsBackupResponse.getStatusCode().value() + " [" + zeebeEsBackupResponse.getBody() + "]");
    }

    /**
     * Monitor and wait for the end of the backup
     * @param backupId backupId to monitor and wait
     * @param operationLog report status
     */
    public void monitorEsBackup(Long backupId, OperationLog operationLog) {
        ZeebeInformation zeebeInformation = getInformation();
        String zeebeEsRepository = blueberryConfig.getZeebeRecordRepository();

        ResponseEntity<ZeebeBackupStatusResponse> backupStatusResponse = null;
        do {
            logger.info("checking backup status for url {}", blueberryConfig.getZeebeActuatorUrl());
            backupStatusResponse = restTemplate.getForEntity(blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + blueberryConfig.getZeebeRepository() + "/_status?pretty", ZeebeBackupStatusResponse.class);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                // Do nothing
            }
            logger.info("backup status response for url {}: {}, {}", blueberryConfig.getElasticsearchUrl(), backupStatusResponse.getStatusCodeValue(), backupStatusResponse.getBody());

        } while (backupStatusResponse.getStatusCode().is2xxSuccessful() &&
                backupStatusResponse.getBody() != null && !backupStatusResponse.getBody().getSnapshots().get(0).getState().equals("SUCCESS")

        );
    }


    /* ******************************************************************** */
    /*                                                                      */
    /*  Backup section                                                      */
    /*                                                                      */
    /* ******************************************************************** */

    public void backup(Long backupId, OperationLog operationLog) {
        Map<String, Object> backupBody = Map.of("backupId", backupId);
        ResponseEntity<String> backupResponse = restTemplate.postForEntity(blueberryConfig.getZeebeActuatorUrl() + "/actuator/backups", backupBody, String.class);
        logger.info("backup status response for url {}: {}, {}", blueberryConfig.getZeebeActuatorUrl(), backupResponse.getStatusCodeValue(), backupResponse.getBody());

    }

    public void monitorBackup(Long backupId, OperationLog operationLog) {
        boolean written = false;
        ResponseEntity<BackupStatusResponse> backupStatusResponse = null;
        do {
            logger.info("checking backup status for url {}", blueberryConfig.getZeebeActuatorUrl());
            try {
                Thread.sleep(10_000L);
            } catch (InterruptedException e) {
                // Do nothing
            }
            backupStatusResponse = restTemplate.getForEntity(blueberryConfig.getZeebeActuatorUrl() + "/actuator/backups/" + backupId, BackupStatusResponse.class);
            logger.info("backup status response for url {}: {}, {}", blueberryConfig.getZeebeActuatorUrl(), backupStatusResponse.getStatusCodeValue(), backupStatusResponse.getBody());

            if (!written) {
                // operationLog.addSnapshotName("zeebe",backupStatusResponse.getBody());
                written = true; //only write once (this can surely be done better :-))
            }

        } while (backupStatusResponse.getStatusCode().is2xxSuccessful() && backupStatusResponse.getBody() != null /* && !backupStatusResponse.getBody().getState().equals("COMPLETED")*/);
    }

    public ZeebeInformation getInformation() {
        return new ZeebeInformation();
    }


    /* ******************************************************************** */
    /*                                                                      */
    /*  Administration                                                      */
    /*                                                                      */
    /* ******************************************************************** */

    /**
     * https://docs.camunda.io/docs/8.7/self-managed/operational-guides/backup-restore/zeebe-backup-and-restore/#list-backups-api
     */

    public List<BackupInfo> getListBackup() throws OperationException {
        ResponseEntity<BackupStatusResponse> backupStatusResponse = null;
        try {
            logger.info("Execute [{}]", blueberryConfig.getZeebeActuatorUrl() + "/actuator/backups");
            ResponseEntity<JsonNode> listResponse = restTemplate.getForEntity(blueberryConfig.getZeebeActuatorUrl() + "/actuator/backups", JsonNode.class);
            JsonNode jsonArray = listResponse.getBody();

            List<BackupInfo> listBackupInfo = StreamSupport.stream(jsonArray.spliterator(), false)
                    .map(t -> {
                        BackupInfo backupInfo = new BackupInfo();
                        backupInfo.backupId = t.get("backupId").asInt();
                        backupInfo.status = BackupInfo.fromZeebeStatus(BackupStatusCode.valueOf(t.get("state").asText()));
                        // search the date in the first partition
                        JsonNode[] details = objectMapper.convertValue(t.get("details"), JsonNode[].class);

                        String timestamp = details[0].get("createdAt").asText();
                        LocalDateTime localDateTime = Instant.parse(timestamp)
                                .atZone(ZoneId.systemDefault()) // Convert to system's time zone
                                .toLocalDateTime();

                        backupInfo.backupTime = localDateTime;
                        return backupInfo;
                    }).toList();
            logger.info("Found {} backups", listBackupInfo.size());

            return listBackupInfo;
        } catch (Exception e) {
            throw OperationException.getInstanceFromException(OperationException.BLUEBERRYERRORCODE.BACKUP_LIST, e);
        }
    }

    public class ZeebeInformation {
        public int clusterSize;
        public int numberOfPartitions;
        public int replicaFactor;
    }
}
