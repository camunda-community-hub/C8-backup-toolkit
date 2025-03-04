package io.camunda.blueberry.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(value = "blueberry")
@Component
public class BlueberryConfig {

    @Value("${blueberry.optimizeurl:@null}")
    private String optimizeUrl;

    @Value("${blueberry.operateurl:@null}")
    private String operateUrl;

    @Value("${blueberry.tasklisturl:@null}")
    private String tasklistUrl;

    @Value("${blueberry.zeebeactuatorurl:http://localhost:9600}")
    private String zeebeActuatorUrl;

    @Value("${blueberry.elasticsearchurl:http://localhost:9200}")
    private String elasticsearchUrl;

    public String getOptimizeUrl() {
        return optimizeUrl;
    }

    public String getOperateUrl() {
        return operateUrl;
    }

    public String getTasklistUrl() {
        return tasklistUrl;
    }

    public String getZeebeActuatorUrl() {
        return zeebeActuatorUrl;
    }


    public String getElasticsearchUrl() {
        return elasticsearchUrl;
    }
}
