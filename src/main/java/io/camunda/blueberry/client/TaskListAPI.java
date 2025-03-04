package io.camunda.blueberry.client;

import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.operation.OperationLog;
import io.camunda.zeebe.protocol.impl.encoding.BackupStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class TaskListAPI extends WebActuator {

    Logger logger = LoggerFactory.getLogger(TaskListAPI.class);

    private BlueberryConfig blueberryConfig;

    private RestTemplate restTemplate;

    public TaskListAPI(BlueberryConfig blueberryConfig, RestTemplate restTemplate) {
        super(restTemplate);
        this.blueberryConfig = blueberryConfig;
        this.restTemplate = restTemplate;
    }

    public void connection() {

    }

    public boolean isTaskListExist() {
        return false;
    }

    public void backup(Long backupId, OperationLog operationLog) {
        startBackup("operate", backupId, blueberryConfig.getTasklistUrl(), operationLog);
    }

    public void monitorBackup(Long backupId, OperationLog operationLog) {
        checkBackupStatus(COMPONENT.TASKLIST, backupId, blueberryConfig.getTasklistUrl(), operationLog);
    }

}

