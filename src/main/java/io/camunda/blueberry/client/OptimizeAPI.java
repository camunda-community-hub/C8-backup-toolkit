package io.camunda.blueberry.client;

import io.camunda.blueberry.client.toolbox.KubenetesToolbox;
import io.camunda.blueberry.client.toolbox.WebActuator;
import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.operation.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OptimizeAPI implements CamundaApplication {

    Logger logger = LoggerFactory.getLogger(OptimizeAPI.class.getName());

    private final BlueberryConfig blueberryConfig;
    private final RestTemplate restTemplate;
    private final WebActuator webActuator;
    private final KubenetesToolbox kubenetesToolbox;

    OptimizeAPI(BlueberryConfig blueberryConfig, RestTemplate restTemplate) {
        webActuator = new WebActuator(restTemplate);
        kubenetesToolbox = new KubenetesToolbox();

        this.blueberryConfig = blueberryConfig;
        this.restTemplate = restTemplate;
    }

    public void connection() {
    }


    public boolean exist() {
        String url = blueberryConfig.getOptimizeActuatorUrl();
        return kubenetesToolbox.isPodExist("tasklist");
    }


    public BackupOperation backup(Long backupId, OperationLog operationLog) throws BackupException {
        return webActuator.startBackup(CamundaApplication.COMPONENT.OPTIMIZE, backupId, blueberryConfig.getOptimizeActuatorUrl(), operationLog);
    }

    public void waitBackup(Long backupId, OperationLog operationLog) {
        webActuator.waitBackup(CamundaApplication.COMPONENT.OPTIMIZE, backupId, blueberryConfig.getOptimizeActuatorUrl(), operationLog);
    }

}
