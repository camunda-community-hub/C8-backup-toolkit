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
public class KubernetesAccess extends ContainerAccess{
    Logger logger = LoggerFactory.getLogger(KubernetesAccess.class);

    BlueberryConfig blueberryConfig;
    KubernetesClient client = null;

    KubernetesAccess(BlueberryConfig blueberryConfig) {
        this.blueberryConfig = blueberryConfig;
    }

    /**
     * Connect to the Kubernetes cluster
     * @return
     */
    public OperationResult connection() {
        OperationResult result = new OperationResult();
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
            result.success=true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.success=false;
            result.details= "Can't connect to Kubernetes";
        }
        return result;
    }


    public boolean isConnected() {
        return client != null;
    }

    /**
     * return true tif the component exist in the cluster
     * @param component to check
     * @param nameSpace namespace
     * @return the result. operationResult.resultBoolean is true if the component exist
     *
     */
    public OperationResult existComponent(CamundaApplication.COMPONENT component, String nameSpace) {
        OperationResult operationResult = new OperationResult();
        operationResult.success=true;
        operationResult.command="kubectl get pods | grep "+component.toString();
        operationResult.resultBoolean=true;
        return operationResult;
    }

    /**
     * Explore the component to retrieve the repository. For example, Operate defined a variable CAMUNDA_OPERATE_BACKUP_REPOSITORY_NAME
     * @param component component to explore
     * @param nameSpace namespace
     * @return the result
     */
    public OperationResult getRepositoryName(CamundaApplication.COMPONENT component, String nameSpace)  {
        // Soon: ask the pod informatoin and read it from it
        OperationResult operationResult = new OperationResult();
        switch (component) {
            case OPERATE:
                operationResult.resultSt=blueberryConfig.getOperateRepository();
                operationResult.success=true;
                operationResult.command = "kubectl describe pod operate";
                break;

            case TASKLIST:
                operationResult.resultSt= blueberryConfig.getTasklistRepository();
                operationResult.success=true;
                operationResult.command = "kubectl describe pod tasklist";
                break;

            case OPTIMIZE:
                operationResult.resultSt = blueberryConfig.getOptimizeRepository();
                operationResult.success=true;
                operationResult.command = "kubectl describe pod optimize";
                break;


            default:
                operationResult.success = false;
                operationResult.details = "Unknown component";
                break;
        }
        return operationResult;
    }


    /**
     * List all pods containing the name <podName> in a given namespace.
     * @param podName filter pods by the name (contains: a deployment add ID after the name)
     * @param namespace namespace to search
     * @return list of pods
     * @throws KubernetesException
     */
    private List<Pod> getContainerInformation(String podName, String namespace) throws KubernetesException {

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