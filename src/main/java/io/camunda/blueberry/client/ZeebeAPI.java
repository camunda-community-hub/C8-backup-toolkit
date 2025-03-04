package io.camunda.blueberry.client;

import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.OperationException;
import io.camunda.blueberry.operation.OperationLog;
import io.camunda.zeebe.protocol.impl.encoding.BackupListResponse;
import io.camunda.zeebe.protocol.impl.encoding.BackupStatusResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ZeebeAPI extends WebActuator{

    private BlueberryConfig blueberryConfig;
    private RestTemplate restTemplate;

    public ZeebeAPI(BlueberryConfig blueberryConfig, RestTemplate restTemplate) {
        super(restTemplate);
        this.blueberryConfig = blueberryConfig;
        this.restTemplate = restTemplate;
    }    public void connection() {

    }


    /* ******************************************************************** */
    /*                                                                      */
    /*  Resume and pause                                                    */
    /*                                                                      */
    /* ******************************************************************** */

    public void pauseExporting(OperationLog operationLog)
    {
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

    /**
     * curl -X PUT http://localhost:9200/_snapshot/zeeberecordrepository/12 -H 'Content-Type: application/json'   \
     *         -d '{ "indices": "zeebe-record*", "feature_states": ["none"]}'
     * @param backupId
     * @param operationLog
     */
    public void esBackup(Long backupId, OperationLog operationLog){
        ZeebeInformation zeebeInformation = getInformation();
        String zeebeEsRepository= zeebeInformation.esRepository;

        HttpEntity<?> zeebeEsBackupRequest = new HttpEntity<>(Map.of("indices", "zeebe-record*", "feature_states", List.of("none")));
        ResponseEntity<String> zeebeEsBackupResponse = restTemplate.exchange(blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + zeebeEsRepository + "/" + backupId, HttpMethod.PUT, zeebeEsBackupRequest, String.class);
        operationLog.info("Start Zeebe ES Backup on ["+zeebeEsRepository+"] response: "+zeebeEsBackupResponse.getStatusCode().value()+" ["+ zeebeEsBackupResponse.getBody()+"]");
    }

    public void monitorEsBackup(Long backupId, OperationLog operationLog) {
        ZeebeInformation zeebeInformation = getInformation();
        String zeebeEsRepository= zeebeInformation.esRepository;

        ResponseEntity<ZeebeBackupStatusResponse> backupStatusResponse = null;
        do {
            logger.info("checking backup status for url {}", blueberryConfig.getZeebeActuatorUrl());
            backupStatusResponse = restTemplate.getForEntity(blueberryConfig.getElasticsearchUrl() + "/_snapshot/" + zeebeEsRepository + "/_status?pretty", ZeebeBackupStatusResponse.class);
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

            if(!written) {
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
            logger.info("Execute [{}]",blueberryConfig.getZeebeActuatorUrl() + "/actuator/backups");
            ResponseEntity<BackupListResponse> listResponse = restTemplate.getForEntity(blueberryConfig.getZeebeActuatorUrl() + "/actuator/backups", BackupListResponse.class);
            List<BackupInfo> listBackupInfo = listResponse.getBody().getBackups().stream()
                    .map(t->{ BackupInfo info = new BackupInfo();
                        info.backupId = t.backupId();
                        info.backupName = "";
                        info.status = BackupInfo.fromZeebeStatus(t.status());
                        return info;
                    }).collect(Collectors.toList());

            return listBackupInfo;
        } catch (Exception e) {
            throw new OperationException("BACKUP_LIST", e.getMessage());
        }
    }

    public class ZeebeInformation {
        public int clusterSize;
        public int numberOfPartitions;
        public int replicaFactor;
        public String esRepository;
    }

}
