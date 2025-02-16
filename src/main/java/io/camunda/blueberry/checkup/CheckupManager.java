package io.camunda.blueberry.checkup;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class CheckupManager {

    /**
     * Check the system
     * Does Zeebe declare a container? A Type storage?
     * Does Elasticsearch as repository for each component like OperateClient?
     *
     * @return
     */
    public Map<String, Object> checkup() {
        return Collections.emptyMap();
    }
}
