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

    @Value("${blueberry.namespace:camunda}")
    private String namespace;


    @Value("${blueberry.kubeConfig:~/.kube/config}")
    private String kubeConfig;

    @Value("${blueberry.elasticsearch.containerType:}")
    private String elasticsearchContainerType;
    @Value("${blueberry.elasticsearch.containerName:}")
    private String elasticsearchContainerName;
    @Value("${blueberry.elasticsearch.operateContainerBasePath:/operate}")
    private String operateContainerBasePath;
    @Value("${blueberry.elasticsearch.tasklistContainerBasePath:/tasklist}")
    private String tasklistContainerBasePath;
    @Value("${blueberry.elasticsearch.optimizeContainerBasePath:/optimize}")
    private String optimizeContainerBasePath;
    @Value("${blueberry.elasticsearch.zeebeRecordContainerBasePath:/zeeberecord}")
    private String zeebeRecordContainerBasePath;


    @Value("${blueberry.zeebe.zeebeRecordRepository:camunda_zeebe_records_backup}")
    private String zeebeRecordRepository;

    /**
     * These values are temporary. KubernetesAccess should get that values directly from pôds
     */
    @Value("${blueberry.operateRepository}")
    private String operateRepository;
    @Value("${blueberry.tasklistRepository}")
    private String tasklistRepository;
    @Value("${blueberry.optimizeRepository}")
    private String optimizeRepository;


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

    public String getNamespace() {
        return namespace;
    }

    public String getElasticsearchContainerName() {
        return elasticsearchContainerName;
    }

    public String getElasticsearchContainerType() {
        return elasticsearchContainerType;
    }

    public String getOperateContainerBasePath() {
        return operateContainerBasePath;
    }

    public String getZeebeRecordRepository() {
        return zeebeRecordRepository;
    }

    public String getOptimizeContainerBasePath() {
        return optimizeContainerBasePath;
    }

    public String getTasklistContainerBasePath() {
        return tasklistContainerBasePath;
    }

    public String getZeebeRecordContainerBasePath() {
        return zeebeRecordContainerBasePath;
    }

    public String getKubeConfig() {
        return kubeConfig;
    }

    public String getOperateRepository() {
        return operateRepository;
    }

    public String getTasklistRepository() {
        return tasklistRepository;
    }

    public String getOptimizeRepository() {
        return optimizeRepository;
    }
}
