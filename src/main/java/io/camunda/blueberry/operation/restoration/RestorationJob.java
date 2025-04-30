package io.camunda.blueberry.operation.restoration;

import io.camunda.blueberry.connect.ElasticSearchConnect;
import io.camunda.blueberry.connect.KubernetesConnect;
import io.camunda.blueberry.exception.KubernetesException;
import io.camunda.blueberry.operation.OperationLog;
import io.camunda.blueberry.operation.backup.BackupJob;

import java.io.IOException;

/**
 * Handles the restoration process for a specific backup ID.
 */
public class RestorationJob {

    private final OperationLog operationLog;
    private BackupJob.JOBSTATUS jobStatus = BackupJob.JOBSTATUS.PLANNED;
    private final long backupId;

    private final KubernetesConnect kubernetesConnect;
    private final RestorationManager restoreManager;
    private final ElasticSearchConnect elasticSearchConnect;
    private final RestorationPod restorationPod;

    /**
     * Constructor to initialize the restoration job with a given backup ID.
     * @param backupId The ID of the backup to restore.
     * @param kubernetesConnect K8s connection for scaling operations.
     * @param restoreManager Manager for handling restore pods.
     * @param elasticSearchConnect Connection for Elasticsearch restoration.
     */
    public RestorationJob(long backupId, KubernetesConnect kubernetesConnect, RestorationManager restoreManager, ElasticSearchConnect elasticSearchConnect, RestorationPod restorationPod) {
        this.restorationPod = restorationPod;
        this.operationLog = new OperationLog();
        this.backupId = backupId;
        this.kubernetesConnect = kubernetesConnect;
        this.restoreManager = restoreManager;
        this.elasticSearchConnect = elasticSearchConnect;
    }

    public BackupJob.JOBSTATUS getJobStatus() {
        return jobStatus;
    }

    /**
     * Runs the restoration process asynchronously.
     */
    public void restoration() throws RestorationException, KubernetesException, IOException {
        operationLog.startOperation("Restoration", 8);  // Adjusted to 8 steps
        this.jobStatus = BackupJob.JOBSTATUS.INPROGRESS;

        long beginTime = System.currentTimeMillis();
        operationLog.info("Start Restoration for backup ID: " + backupId);

        // Step 1: Scale down Zeebe
        operationLog.operationStep(1, "Scaling down Zeebe");
        restoreManager.scaleDownZeebe(operationLog);

        // Step 2: Delete all Elasticsearch indices
        operationLog.operationStep(2, "Deleting all Elasticsearch indices");
        elasticSearchConnect.deleteAllNonSystemIndices(operationLog);

        // Step 3: Restore Elasticsearch
        operationLog.operationStep(3, "Restoring Elasticsearch backup");
        elasticSearchConnect.restoreSnapshots(backupId, operationLog);

        // Step 4: Create and start restore pods
        operationLog.operationStep(4, "Creating and starting restore pods");
        restorationPod.createPodRestoration(operationLog); // Generate Zeebe Restore Job Yaml

        // Step 5: Monitor restore pods
        operationLog.operationStep(5, "Monitoring restore pods");
        restoreManager.logKubectlGetLogsCommand(operationLog);

        // Step 6: Stop restoration pods
        operationLog.operationStep(6, "Stopping restoration pods");
        restoreManager.restoreDeleteJobs(operationLog);

        // Step 7: Scale up Zeebe
        operationLog.operationStep(7, "Scaling up Zeebe");
        restoreManager.scaleUpZeebe(operationLog);

        operationLog.info("Restoration completed in " + (System.currentTimeMillis() - beginTime) + " ms");
        operationLog.endOperation();
        this.jobStatus = BackupJob.JOBSTATUS.COMPLETED;
    }
    /**
     * Enum representing the possible states of the restoration job.
     */
    enum JOBSTATUS { PLANNED, INPROGRESS, COMPLETED, FAILED }


    public static class RestorationException extends Exception {
        public RestorationException(String message) {
            super(message);
        }
    }
}