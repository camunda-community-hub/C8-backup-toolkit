package io.camunda.blueberry.client;


import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.KubernetesException;
import io.camunda.blueberry.exception.OperationException;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class KubernetesAccess {
    Logger logger = LoggerFactory.getLogger(KubernetesAccess.class);

    BlueberryConfig blueberryConfig;
    KubernetesClient client = null;

    KubernetesAccess(BlueberryConfig blueberryConfig) {
        this.blueberryConfig = blueberryConfig;
    }

    public void connection() throws KubernetesException {
        try {
            if (blueberryConfig.getKubeConfig() != null) {
                logger.info("Connection to Kubernetes via KubeConfig[{}]", blueberryConfig.getKubeConfig());
                Config config = Config.fromKubeconfig(new java.io.File(blueberryConfig.getKubeConfig()));
                client = new KubernetesClientBuilder().withConfig(config).build();
            }
            else {
                logger.info("Connection to Kubernetes via Default Client");
                client = new DefaultKubernetesClient();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new KubernetesException(OperationException.BLUEBERRYERRORCODE.KUBERNETES_CLIENT, 400, "Can't access Kubernetes", "Can't connect to Kubernetes using");

        }
    }


    public boolean isConnected() {
        return client != null;
    }

    public String getRepositoryName(CamundaApplication.COMPONENT component, String nameSpace)  throws KubernetesException {
        // Soon: ask the pod informatoin and read it from it
        switch (component) {
            case OPERATE:
                return blueberryConfig.getOperateRepository();
            case TASKLIST:
                return blueberryConfig.getTasklistRepository();
            case OPTIMIZE:
                return blueberryConfig.getOptimizeRepository();
        }
        return null;
    }

    public List<Pod> getContainerInformation(String podName, String namespace) throws KubernetesException {

        try {
            // Namespace where the pods are running

            // Get all pods in the namespace
            List<Pod> pods = client.pods().inNamespace(namespace).list().getItems();

            // Print pod names
            for (Pod pod : pods) {
                logger.info("Pods {}", pod.getMetadata().getName());
            }
            return pods;
        } catch (Exception e) {
            throw new KubernetesException(OperationException.BLUEBERRYERRORCODE.CHECK, 400, "Can't list pods", "Pods can't be listed in namespace[" + namespace + "] "+e.getMessage());
        }
    }

}