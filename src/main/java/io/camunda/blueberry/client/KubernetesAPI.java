package io.camunda.blueberry.client;



import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;


import java.io.IOException;
import java.util.List;


public class KubernetesAPI {

    public void getContainerInformation(String podName, String namespace) {

        try (KubernetesClient client = new DefaultKubernetesClient()) {
            // Namespace where the pods are running

            // Get all pods in the namespace
            List<Pod> pods = client.pods().inNamespace(namespace).list().getItems();

            // Print pod names
            for (Pod pod : pods) {
                System.out.println("Pod Name: " + pod.getMetadata().getName());
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}