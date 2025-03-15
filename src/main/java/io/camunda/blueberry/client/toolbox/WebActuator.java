package io.camunda.blueberry.client.toolbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.blueberry.client.ActuatorBackupStatusResponse;
import io.camunda.blueberry.client.CamundaApplication;
import io.camunda.blueberry.operation.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class WebActuator {
    Logger logger = LoggerFactory.getLogger(WebActuator.class);

    private RestTemplate restTemplate = new RestTemplate();

    public WebActuator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }




    /**
     * Start the backup command
     * Example curl -X POST http://localhost:8081/actuator/backup -H "Content-Type: application/json" -d '{"backupId": 12}'
     * @param component component to communicate with
     * @param backupId Backup ID
     * @param url complete url to start the backup command in the component
     * @param operationLog log the operation
     */
    public CamundaApplication.BackupOperation startBackup(CamundaApplication.COMPONENT component, Long backupId, String url, OperationLog operationLog) {
        Map<String, Object> backupBody = java.util.Map.of("backupId", backupId);

        String urlComplete=url + "/actuator/backups";
        CamundaApplication.BackupOperation backupOperation = new CamundaApplication.BackupOperation();
        try {
            logger.info("StartBackup component[{}] with url[{}] body: {} ", component, urlComplete, backupBody.toString() );
            // We use a JsonNode to be more robust, in case the result change in a version. So, we can explore the JSON result.
            ResponseEntity<JsonNode> backupResponse = restTemplate.postForEntity(urlComplete, backupBody, JsonNode.class);

            backupOperation.status=backupResponse.getStatusCode().value();
            if (backupOperation.status != 200) {
                operationLog.error("backup [" + backupId + "] status[" + backupOperation.status + "] url [" + url + "]");
                return backupOperation;
            }
            // Extract the list of Snapshoot
            JsonNode bodyJson = backupResponse.getBody();
            if (bodyJson.has("scheduledSnapshots")) { // Check if scheduledSnapshots exists
                JsonNode snapshotsNode = bodyJson.get("scheduledSnapshots");

                if (snapshotsNode != null && snapshotsNode.isArray()) {
                    for (JsonNode node : snapshotsNode) {
                        backupOperation.listSnapshots.add(node.asText());
                    }
                }
                operationLog.info("backup [" + backupId + "] url[" + url + "] ListBackup[" + backupOperation.listSnapshots + "]");
            } else if (bodyJson.has("message")) { // Handle Optimize case where "message" exists
                backupOperation.information = bodyJson.get("message").asText();
                operationLog.info(String.format("backup [%d] url[%s] Message[%s]", backupId, url, backupOperation.information));
            } else {
                operationLog.warning(String.format("backup [%d] url[%s] Unexpected response: %s", backupId, url, bodyJson.toString()));
            }
        } catch(Exception e) {
            String messageSimplified = decodeMessage(e.getMessage());
            backupOperation.information = messageSimplified;
            backupOperation.detailInformation=  "Url["+urlComplete+"] Error code[" + e.getMessage() + "]";
            operationLog.error("CamundaApplication["+component.toString()+"] backup [" + backupId + "] Error[" +messageSimplified + "] url [" + url + "]");
        }
        return backupOperation;

    }

    /**
     * Check the backup status
     * @param component component to communicate with
     * @param backupId Backup ID
     * @param url complete url to start the backup command in the component
     * @param operationLog log the operation
     */
    public void waitBackup(CamundaApplication.COMPONENT component, long backupId, String url,
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


    // get something like
    // 400 : "{"status":400,"message":"
    // A backup with ID [13] already exists. Found snapshots: [camunda_operate_13_8.6.9_part_1_of_6/NPRiYy8bTE6H2iVd1H18mQ, camunda_operate_13_8.6.9_part_2_of_6/02ecFnQqQWSz4gGUfKm-2w, camunda_operate_13_8.6.9_part_3_of_6/jJv3m-okTA-yHIzQeaB6Hw, camunda_operate_13_8.6.9_part_4_of_6/Hj7uk7FaTpaKaiAY6qMhwQ, camunda_operate_13_8.6.9_part_5_of_6/jCKZmlCeR5KQae1AiuE0FA, camunda_operate_13_8.6.9_part_6_of_6/lG6skgQqQhqW2TTr2ztg6Q]","instance":"64bc866d-0b04-4692-aabe-a159e8779d87","type":"Invalid request"}"

    private String decodeMessage(String messageComplete) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(messageComplete);

            // Extract the "message" field
            String message = rootNode.get("message").asText();
            return message;
        } catch (Exception e) {
            logger.error("Error decoding message", e);
        }
        return messageComplete;
    }
}
