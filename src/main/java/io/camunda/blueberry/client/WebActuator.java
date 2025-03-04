package io.camunda.blueberry.client;

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

    public void startBackup(String component, Long backupId, String url, OperationLog operationLog) {
        Map<String, Object> backupBody = java.util.Map.of("backupId", backupId);
        ResponseEntity<String> backupResponse = restTemplate.postForEntity(url + "/actuator/backups", backupBody, String.class);
        operationLog.info("backup [" + backupId + "] status[" + backupResponse.getStatusCode().value() + "] url [" + url + "] Body[" + backupResponse.getBody() + "]");
    }

    public void checkBackupStatus(COMPONENT component, long backupId, String url,
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
