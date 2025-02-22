package io.camunda.blueberry.operation.backup;

import io.camunda.blueberry.client.ElasticsearchAPI;
import io.camunda.blueberry.client.OperateAPI;
import io.camunda.blueberry.client.OptimizeAPI;
import io.camunda.blueberry.client.TaskListAPI;
import io.camunda.blueberry.operation.OperationLog;
import io.camunda.zeebe.client.ZeebeClient;

/**
 * Collect all operation of the backup. This is returned to the monitoring, or at the end of the execution
 */
public class BackupJob {

    OperationLog operationLog;
    private final OperateAPI operateAPI;
    private final TaskListAPI taskListAPI;
    private final OptimizeAPI optimizeClient;
    private final ElasticsearchAPI elasticsearchAPI;
    private final ZeebeClient zeebeClient;
    private JOBSTATUS jobStatus = JOBSTATUS.PLANNED;
    private long backupId;

    protected BackupJob(OperateAPI operateAPI,
                        TaskListAPI taskListAPI,
                        OptimizeAPI optimizeClient,
                        ElasticsearchAPI elasticsearchAPI,
                        ZeebeClient zeebeClient) {
        this.operateAPI = operateAPI;
        this.taskListAPI = taskListAPI;
        this.optimizeClient = optimizeClient;
        this.elasticsearchAPI = elasticsearchAPI;
        this.zeebeClient = zeebeClient;
    }

    public JOBSTATUS getJobStatus() {
        return jobStatus;
    }

    public OperationLog getLog() {
        return operationLog;
    }

    public long getBackupId() {
        return backupId;
    }

    /**
     * Start a backup
     */
    public void backup(long backupId) {
        long beginTime = System.currentTimeMillis();
        operationLog.info("Start Backup");
        jobStatus = JOBSTATUS.INPROGRESS;
        this.backupId = backupId; // calculate a new backup
        // backup Operate
        if (operateAPI.isOperateExist()) {
            operationLog.info("Start Operate Backup");
        }
        // backup TaskList
        if (taskListAPI.isTaskListExist()) {
            // Srart the backup
        }
        // backup Optimize


        // Wait end of backup Operate, TaskList, Optimize, Zeebe

        // Stop Zeebe imported


        // backup Zeebe record
        // backup Zeebe

        // Finish? Then stop all restoration pod

        // scale up Zeebe

        operationLog.info("End of backup in " + (System.currentTimeMillis() - beginTime) + " ms");
        jobStatus = JOBSTATUS.COMPLETED;
    }

    public enum JOBSTATUS {PLANNED, INPROGRESS, COMPLETED, FAILED}
}
