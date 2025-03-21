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
    private String elasticsearchContainerName;

    @Value("${blueberry.elasticsearch.containerName:}")
    private String elasticsearchContainerType;

    @Value("${blueberry.elasticsearch.operateContainerBasePath:/operatebackup}")
    private String operateContainerBasePath;

    @Value("${blueberry.operateRepository}")
    private String operateRepository;

    @Value("${blueberry.tasklistRepository}")
    private String tasklistRepository;

    @Value("${blueberry.optimizeRepository}")
    private String optimizeRepository;

    @Value("${blueberry.zeebeRepository}")
    private String zeebeRepository;

    // S3 specific configuration
    @Value("${blueberry.s3.bucket:}")
    private String s3Bucket;

    @Value("${blueberry.s3.basePath:}")
    private String s3BasePath;

    @Value("${blueberry.s3.region:}")
    private String s3Region;

    // Azure specific configuration
    @Value("${blueberry.azure.container:}")
    private String azureContainer;

    @Value("${blueberry.azure.basePath:}")
    private String azureBasePath;

    @Value("${blueberry.storageType:}")
    private String storageType;

    // Getters for general properties
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

    public String getZeebeRepository() {
        return zeebeRepository;
    }

    // Getters for S3 configuration
    public String getS3Bucket() {
        return s3Bucket;
    }

    public String getS3BasePath() {
        return s3BasePath;
    }

    public String getS3Region() {
        return s3Region;
    }

    // Getters for Azure configuration
    public String getAzureContainer() {
        return azureContainer;
    }

    public String getAzureBasePath() {
        return azureBasePath;
    }

    public String getStorageType(){
        return storageType;
    }
}
