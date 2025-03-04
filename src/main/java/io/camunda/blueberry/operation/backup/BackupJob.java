package io.camunda.blueberry.operation.backup;

import io.camunda.blueberry.client.*;
import io.camunda.blueberry.operation.OperationLog;
import io.camunda.zeebe.client.ZeebeClient;

/**
 * Collect all operation of the backup. This is returned to the monitoring, or at the end of the execution
 */
public class BackupJob {

    OperationLog operationLog;
    private final OperateAPI operateAPI;
    private final TaskListAPI taskListAPI;
    private final OptimizeAPI optimizeAPI;
    private final ZeebeAPI zeebeAPI;
    private JOBSTATUS jobStatus = JOBSTATUS.PLANNED;
    private long backupId;

    protected BackupJob(OperateAPI operateAPI, TaskListAPI taskListAPI, OptimizeAPI optimizeAPI,
                         ZeebeAPI zeebeAPI, OperationLog operationLog) {
        this.operateAPI = operateAPI;
        this.taskListAPI = taskListAPI;
        this.optimizeAPI = optimizeAPI;
        this.zeebeAPI = zeebeAPI;
        this.operationLog = operationLog;
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
            operateAPI.backup(backupId, operationLog);
        }
        // backup TaskList
        if (taskListAPI.isTaskListExist()) {
            taskListAPI.backup(backupId, operationLog);
        }
        // backup Optimize
        if (optimizeAPI.isOptimizeExist()) optimizeAPI.backup(backupId, operationLog);

        // Wait end of backup Operate, TaskList, Optimize, Zeebe
        if (operateAPI.isOperateExist()) {
            operateAPI.monitorBackup(backupId, operationLog);
        }
        if (taskListAPI.isTaskListExist()) {
            taskListAPI.monitorBackup(backupId, operationLog);
        }
        if (optimizeAPI.isOptimizeExist()) {
            optimizeAPI.monitorBackup(backupId, operationLog);
        }

        // Stop Zeebe imported
        zeebeAPI.pauseExporting(operationLog);

        // backup Zeebe record
        zeebeAPI.esBackup(backupId, operationLog);
        zeebeAPI.monitorEsBackup(backupId, operationLog);

        // backup Zeebe
        zeebeAPI.backup(backupId, operationLog);
        zeebeAPI.monitorBackup(backupId, operationLog);

        // Finish? Then stop all restoration pod
        zeebeAPI.resumeExporting(operationLog);



        operationLog.info("End of backup in " + (System.currentTimeMillis() - beginTime) + " ms");
        jobStatus = JOBSTATUS.COMPLETED;
    }

    public enum JOBSTATUS {PLANNED, INPROGRESS, COMPLETED, FAILED}
}
