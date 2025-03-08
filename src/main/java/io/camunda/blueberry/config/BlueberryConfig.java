package io.camunda.blueberry.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(value = "blueberry")
@Component
public class BlueberryConfig {

    @Value("${blueberry.optimizeActuatorUrl:@null}")
    private String optimizeActuatorUrl;

    @Value("${blueberry.operateActuatorUrl:@null}")
    private String operateActuatorUrl;

    @Value("${blueberry.tasklistActuatorUrl:@null}")
    private String tasklistActuatorUrl;

    @Value("${blueberry.zeebeActuatorUrl:http://localhost:9600}")
    private String zeebeActuatorUrl;

    @Value("${blueberry.elasticsearchurl:http://localhost:9200}")
    private String elasticsearchUrl;

    public String getOptimizeActuatorUrl() {
        return optimizeActuatorUrl;
    }

    public String getOperateActuatorUrl() {
        return operateActuatorUrl;
    }

    public String getTasklistActuatorUrl() {
        return tasklistActuatorUrl;
    }

    public String getZeebeActuatorUrl() {
        return zeebeActuatorUrl;
    }


    public String getElasticsearchUrl() {
        return elasticsearchUrl;
    }
}
