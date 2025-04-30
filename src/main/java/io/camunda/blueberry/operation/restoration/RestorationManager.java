package io.camunda.blueberry.operation.restoration;

import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.connect.KubernetesConnect;
import io.camunda.blueberry.operation.OperationLog;
import io.camunda.blueberry.operation.backup.BackupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The restoration manager are in chage of one restoration
 */
@Component
public class RestorationManager {
    Logger logger = LoggerFactory.getLogger(BackupManager.class);

    private final KubernetesConnect kubernetesConnect;
    private final BlueberryConfig blueberryConfig;

    public RestorationManager(KubernetesConnect kubernetesConnect, BlueberryConfig blueberryConfig) {
        this.kubernetesConnect = kubernetesConnect;
        this.blueberryConfig = blueberryConfig;
    }

    public void scaleDownZeebe(OperationLog operationLog) {
        // Define components and their Kubernetes types
        Map<String, String> components = new HashMap<>();
        components.put("camunda-zeebe", "sts");
        components.put("camunda-zeebe-gateway", "deploy");
        components.put("camunda-operate", "deploy");
        components.put("camunda-tasklist", "deploy");
        components.put("camunda-optimize", "deploy");

        // Iterate over components to generate scale-down commands
        for (Map.Entry<String, String> entry : components.entrySet()) {
            String kubectlCommand = String.format("kubectl scale %s/%s --replicas=0", entry.getValue(), entry.getKey());
            // Print the command to the console or sysout
            System.out.println(kubectlCommand);
            logger.info("Generated scale down command: {}", kubectlCommand);
        }

        // Optionally log the operation if OperationLog is provided
        if (operationLog != null) {
            operationLog.info("Scaling down Zeebe components to 0 replicas.");
        }
    }


    public void scaleUpZeebe(OperationLog operationLog) {
        // Define the components and their Kubernetes types
        Map<String, String> components = new HashMap<>();
        components.put("camunda-zeebe", "sts");  // StatefulSet for camunda-zeebe
        components.put("camunda-zeebe-gateway", "deploy");  // Deployment for camunda-zeebe-gateway
        components.put("camunda-operate", "deploy");  // Deployment for camunda-operate
        components.put("camunda-tasklist", "deploy");  // Deployment for camunda-tasklist
        components.put("camunda-optimize", "deploy");  // Deployment for camunda-optimize

        // Iterate over the components to generate the scale-up commands with dynamic replica counts
        for (Map.Entry<String, String> entry : components.entrySet()) {
            // Get the replica count dynamically using KubernetesConnect
            int replicaCount = kubernetesConnect.getReplicaCount(entry.getKey());

            // Construct the kubectl scale command
            String kubectlScaleCommand = String.format("kubectl scale %s/%s --replicas=%d", entry.getValue(), entry.getKey(), replicaCount);
            // Print the kubectl scale command to the console or sysout
            System.out.println(kubectlScaleCommand);
            logger.info("Generated scale up command: {}", kubectlScaleCommand);

            // Execute the scale command (optional: you can use Runtime.exec or Kubernetes client)
            // runtime.exec(kubectlScaleCommand);

            // If the component is a StatefulSet (like camunda-zeebe), we need to check its rollout status
            if ("sts".equals(entry.getValue())) {
                String rolloutStatusCommand = String.format("kubectl rollout status sts/%s", entry.getKey());
                // Print the rollout status command to the console or sysout
                System.out.println(rolloutStatusCommand);
                logger.info("Generated rollout status command: {}", rolloutStatusCommand);

                // Execute the rollout status command (optional: you can use Runtime.exec or Kubernetes client)
                // runtime.exec(rolloutStatusCommand);
            }
        }

        // Optionally log the operation if OperationLog is provided
        if (operationLog != null) {
            operationLog.info("Scaling up Zeebe components to their respective replica counts.");
        }
    }

    protected void logKubectlGetLogsCommand(OperationLog operationLog) {
        int replicaCount = blueberryConfig.getCamundaZeebeReplicas();
        for (int brokerId = 0; brokerId < replicaCount; brokerId++) {
            String jobName = "zeebe-restore-job-" + brokerId;

            String kubectlGetLogsCommand = String.format(
                    "kubectl logs -f $(kubectl get pods --selector=job-name=%s --output=jsonpath='{.items[*].metadata.name}' | awk '{print $1}')", jobName
            );
            System.out.println(kubectlGetLogsCommand);
            operationLog.info("kubectl get logs command: " + kubectlGetLogsCommand);
        }
    }

    void restoreDeleteJobs(OperationLog operationLog) {
        int replicaCount = blueberryConfig.getCamundaZeebeReplicas();
        StringBuilder kubectlDeleteCommand = new StringBuilder("kubectl delete jobs");

        for (int brokerId = 0; brokerId < replicaCount; brokerId++) {
            String jobName = "zeebe-restore-job-" + brokerId;
            kubectlDeleteCommand.append(" ").append(jobName);
        }

        System.out.println(kubectlDeleteCommand.toString());

        operationLog.info("kubectl delete jobs command: " + kubectlDeleteCommand.toString());
    }
}
