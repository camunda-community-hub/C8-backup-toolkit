package io.camunda.blueberry.client;

import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.operation.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    public void backup(Long backupId, OperationLog operationLog) throws BackupException {
        startBackup(COMPONENT.TASKLIST, backupId, blueberryConfig.getTasklistUrl(), operationLog);
    }

    public void waitBackup(Long backupId, OperationLog operationLog) {
        waitBackup(COMPONENT.TASKLIST, backupId, blueberryConfig.getTasklistUrl(), operationLog);
    }

}

