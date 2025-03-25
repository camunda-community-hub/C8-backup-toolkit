package io.camunda.blueberry.config;


import io.camunda.blueberry.access.CamundaApplication;
import io.camunda.blueberry.access.KubernetesAccess;
import io.camunda.blueberry.access.OperationResult;
import io.camunda.blueberry.platform.rule.RuleOperateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The goal of this class is to explore the cluster, and save the information.
 * What is the cluster size/partition/repartition factor? What is the repository component per component?
 * No need to ask this information every time we need it, we can just ask every 5 minutes and store them.
 */
@Component
public class ExplorationCluster {
    Logger logger = LoggerFactory.getLogger(ExplorationCluster.class);


    @Autowired
    KubernetesAccess kubernetesAccess;

    @Autowired
    BlueberryConfig blueberryConfig;

    private Map<CamundaApplication.COMPONENT, Object> repositoryPerComponent = new HashMap<>();

    private String namespace = null;
    @Autowired
    private RuleOperateRepository ruleOperateRepository;

    public OperationResult getRepositoryPerComponent() {
        OperationResult operationResult = new OperationResult();
        refresh();
        operationResult.resultMap = repositoryPerComponent.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),  // Convert Component to String
                        Map.Entry::getValue                 // Keep the same value
                ));
        return operationResult;
    }


    private void refresh() {

        // namespace never change, so when we get it, save it
        if (namespace == null) {
            namespace = kubernetesAccess.getCurrentNamespace();
            if (namespace == null) {
                namespace = blueberryConfig.getNamespace();
            }
        }

        kubernetesAccess.connection();
        // Component are present?


        // Components present
        for (CamundaApplication.COMPONENT component : List.of(CamundaApplication.COMPONENT.values())) {
            try {
                // is this component is part of the cluster?

                // Yes, then get the list
                repositoryPerComponent.put(CamundaApplication.COMPONENT.OPERATE, kubernetesAccess.getRepositoryNameV2(CamundaApplication.COMPONENT.OPERATE, namespace));
            } catch(Exception e) {
                logger.error("Can't get result per component {}", e.getMessage());
            }
        }

        // Partitions, clusterSize



    }
}
