package io.camunda.blueberry.client;

import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.operation.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OptimizeAPI extends WebActuator {

    Logger logger = LoggerFactory.getLogger(OptimizeAPI.class.getName());

    BlueberryConfig blueberryConfig;
    private RestTemplate restTemplate;

    OptimizeAPI(BlueberryConfig blueberryConfig, RestTemplate restTemplate) {
        super(restTemplate);
        this.blueberryConfig = blueberryConfig;
        this.restTemplate = restTemplate;
    }

    public void connection() {
    }


    public boolean isOptimizeExist() {
        String url = blueberryConfig.getOptimizeUrl();
        return (url != null && url.isEmpty());
    }


    public void backup(Long backupId, OperationLog operationLog) throws BackupException {
        startBackup(COMPONENT.OPTIMIZE, backupId, blueberryConfig.getOptimizeUrl(), operationLog);
    }

    public void waitBackup(Long backupId, OperationLog operationLog) {
        waitBackup(COMPONENT.OPTIMIZE, backupId, blueberryConfig.getOptimizeUrl(), operationLog);
    }

}
