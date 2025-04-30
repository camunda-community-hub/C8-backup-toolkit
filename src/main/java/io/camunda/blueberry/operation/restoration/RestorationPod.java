package io.camunda.blueberry.operation.restoration;

import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.connect.KubernetesConnect;
import io.camunda.blueberry.operation.OperationLog;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RestorationPod {
    private final KubernetesConnect kubernetesConnect;
    private final BlueberryConfig blueberryConfig;

    public RestorationPod(KubernetesConnect kubernetesConnect, BlueberryConfig blueberryConfig) {
        this.kubernetesConnect = kubernetesConnect;
        this.blueberryConfig = blueberryConfig;
    }

    public void createPodRestoration(OperationLog operationLog) throws IOException {
        int replicaCount = kubernetesConnect.getReplicaCount("camunda-zeebe"); // Get the replica count for camunda-zeebe
        String outputDirectoryPath = "/mnt/zeebe/restore_jobs/"; // Directory where YAML files will be saved

        // Loop through each broker and generate a restore job YAML for each
        for (int brokerId = 0; brokerId < replicaCount; brokerId++) {
            // Generate the YAML for the specific broker
            String outputFilePath = outputDirectoryPath + "zeebe-restore-job-" + brokerId + ".yaml";
            generateYamlForBroker(brokerId, replicaCount, outputFilePath);

            // Log the generated YAML file path
            operationLog.info("Generated Zeebe restore job YAML: " + outputFilePath);

            // Print the kubectl apply command for the generated YAML
            String kubectlApplyCommand = String.format("kubectl apply -f %s", outputFilePath);
            System.out.println(kubectlApplyCommand); // Print the kubectl apply command to the console
            operationLog.info("kubectl apply command: " + kubectlApplyCommand); // Log the kubectl apply command
        }
    }
    // Helper method to generate the YAML file for each broker
    protected void generateYamlForBroker(int brokerId, int replicaCount, String outputFilePath) throws IOException {
        // Load the template
        String template = loadTemplate("templates/zeebe-restore-job-template.yaml");

        // Replace placeholders with actual values
        String generatedYaml = template
                .replace("${zeebeVersion}", String.valueOf(blueberryConfig.getZeebeVersion()))
                .replace("${brokerId}", String.valueOf(brokerId))
                .replace("${nodeId}", String.valueOf(brokerId))
                .replace("${partitionsCount}", String.valueOf(blueberryConfig.getCamundaPartitionCount()))
                .replace("${clusterSize}", String.valueOf(replicaCount))
                .replace("${replicationFactor}", String.valueOf(blueberryConfig.getCamundaReplicationFactor()))
                .replace("${pvcName}", "data-camunda-zeebe-" + brokerId)  // PVC name for each broker
                .replace("${s3BasePath}",blueberryConfig.getS3BasePath())
                .replace("${s3BucketName}", blueberryConfig.getS3Bucket())
                .replace("${s3Region}", blueberryConfig.getS3Region());

        // Write the generated YAML to a file
        writeToFile(generatedYaml, outputFilePath);
    }

    // Method to load the YAML template from resources
    private String loadTemplate(String templatePath) throws IOException {
        InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
        return new String(inputStream.readAllBytes());
    }

    // Write the generated YAML content to a file
    private void writeToFile(String content, String outputFilePath) throws IOException {
        Files.write(Paths.get(outputFilePath), content.getBytes());
    }
}
