package io.camunda.blueberry.client;

import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.operation.OperationLog;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
/**
 * Manage communication to OperateAPI
 */
public class OperateAPI extends WebActuator {


    private BlueberryConfig blueberryConfig;
    private RestTemplate restTemplate;

    public OperateAPI(BlueberryConfig blueberryConfig, RestTemplate restTemplate) {
        super(restTemplate);
        this.blueberryConfig = blueberryConfig;
        this.restTemplate = restTemplate;
    }

    public void connection() {

    }


    public boolean isOperateExist() {
        return true;
    }


    public void backup(Long backupId, OperationLog operationLog) throws BackupException {
        startBackup(COMPONENT.OPERATE, backupId, blueberryConfig.getOperateUrl(), operationLog);
    }



public void waitBackup(Long backupId, OperationLog operationLog) {
    waitBackup(COMPONENT.OPERATE, backupId, blueberryConfig.getOperateUrl(), operationLog);
}

/**
 * According to the documentation, Operate has a API to get all backup
 * https://docs.camunda.io/docs/8.7/self-managed/operational-guides/backup-restore/operate-tasklist-backup/#get-backups-list-api
 */
public List<BackupInfo> getListBackup() {
    return Collections.emptyList();
}


}
