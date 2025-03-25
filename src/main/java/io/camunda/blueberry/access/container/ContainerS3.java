package io.camunda.blueberry.access.container;

import org.springframework.stereotype.Component;

@Component
public class ContainerS3 implements Container {

    @Override
    public String getType() {
        return "S3";
    }

    @Override
    public String getElasticsearchPayload(String basePathInsideContainer) {
        return "";
    }

    @Override
    public String getInformation() {
        return "";
    }

}
