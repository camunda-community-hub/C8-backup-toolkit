package io.camunda.blueberry.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.operation.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class WebActuator {
    Logger logger = LoggerFactory.getLogger(WebActuator.class);

    private RestTemplate restTemplate = new RestTemplate();

    protected WebActuator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public enum COMPONENT { TASKLIST, OPERATE, OPTIMIZE, ZEEBERECORD}

    /**
     * Start the backup command
     * Example curl -X POST http://localhost:8081/actuator/backup -H "Content-Type: application/json" -d '{"backupId": 12}'
     * @param component component to communicate with
     * @param backupId Backup ID
     * @param url complete url to start the backup command in the component
     * @param operationLog log the operation
     */
    public void startBackup(COMPONENT component, Long backupId, String url, OperationLog operationLog) throws BackupException {
        Map<String, Object> backupBody = java.util.Map.of("backupId", backupId);

        try {
            logger.info("StartBackup component[{}] with url[{}] ", component, url + "/actuator/backup");
            // We use a JsonNode to be more robust, in case the result change in a version. So, we can explore the JSON result.
            ResponseEntity<JsonNode> backupResponse = restTemplate.postForEntity(url + "/actuator/backup", backupBody, JsonNode.class);
            JsonNode backupStatus = backupResponse.getBody();
            int status = backupStatus.get("status").asInt();
            if (status != 200) {
                operationLog.error("backup [" + backupId + "] status[" + status + "] url [" + url + "]");
                throw new BackupException(component, "Error code[" + status + "]", backupId);
            }
            operationLog.info("backup [" + backupId + "] status[" + backupResponse.getStatusCode().value() + "] url [" + url + "] Body[" + backupResponse.getBody() + "]");
        } catch(Exception e) {
            operationLog.error("backup [" + backupId + "] Error[" +e.getMessage() + "] url [" + url + "]");
            throw new BackupException(component, "Error code[" + e.getMessage() + "]", backupId);
        }

    }

    /**
     * Check the backup status
     * @param component component to communicate with
     * @param backupId Backup ID
     * @param url complete url to start the backup command in the component
     * @param operationLog log the operation
     */
    public void waitBackup(COMPONENT component, long backupId, String url,
                           OperationLog operationLog) {
        boolean written = false;
        ResponseEntity<ActuatorBackupStatusResponse> backupStatusResponse = null;
        do {
            logger.info("checking backup status for url {}", url);
            try {
                Thread.sleep(10_000L);
            } catch (InterruptedException e) {
                // do nothing
            }
            backupStatusResponse = restTemplate.getForEntity(url + "/actuator/backups/" + backupId, ActuatorBackupStatusResponse.class);
            logger.info("backup status response for url {}: {}, {}", url, backupStatusResponse.getStatusCodeValue(), backupStatusResponse.getBody());

            if (!written) {
                for (int i = 0; i < backupStatusResponse.getBody().getDetails().size(); i++) {
                    String csvSnapshotName = backupStatusResponse.getBody().getDetails().get(i).getSnapshotName();
                    operationLog.addSnapshotName(component.toString(), csvSnapshotName);
                }
                written = true; //only write once (this can surely be done better :-))
            }

        } while (backupStatusResponse.getStatusCode().is2xxSuccessful() && backupStatusResponse.getBody() != null && !backupStatusResponse.getBody().getState().equals("COMPLETED"));


    }
}
