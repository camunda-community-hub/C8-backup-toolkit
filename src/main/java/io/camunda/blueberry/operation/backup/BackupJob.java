package io.camunda.blueberry.operation.backup;

import io.camunda.blueberry.client.*;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.operation.OperationLog;

import java.util.List;

/**
 * Collect all operation of the backup. This is returned to the monitoring, or at the end of the execution
 */
public class BackupJob {

    private final OperateAPI operateAPI;
    private final TaskListAPI taskListAPI;
    private final OptimizeAPI optimizeAPI;
    private final ZeebeAPI zeebeAPI;
    OperationLog operationLog;
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
    public void backup(long backupId) throws BackupException {
        operationLog.startOperation("Backup", 7);

        this.jobStatus = JOBSTATUS.INPROGRESS;
        this.backupId = backupId; // calculate a new backup


        // Keep only applications existing in the cluster
        List<CamundaApplication> listApplications = List.of(operateAPI, taskListAPI, optimizeAPI)
                .stream()
                .filter(CamundaApplication::exist)
                .toList();

        // For each application, start the backup
        for (CamundaApplication application : listApplications) {
            CamundaApplication.BackupOperation backupOperation = application.backup(backupId, operationLog);
            if (!backupOperation.isOk()) {
                this.jobStatus = JOBSTATUS.FAILED;
                throw new BackupException(application.getComponent(),
                        400,
                        backupOperation.title,
                        backupOperation.message, backupId);

            }
        }

        // Wait end of backup Operate, TaskList, Optimize, Zeebe
        for (CamundaApplication application : listApplications) {
            application.waitBackup(backupId, operationLog);
        }

        // Stop Zeebe imported
        operationLog.operationStep("Pause Zeebe");
        zeebeAPI.pauseExporting(operationLog);

        // backup Zeebe record
        operationLog.operationStep("Backup Zeebe Elasticsearch");
        zeebeAPI.esBackup(backupId, operationLog);
        zeebeAPI.monitorEsBackup(backupId, operationLog);

        // backup Zeebe
        operationLog.operationStep("Backup Zeebe");
        zeebeAPI.backup(backupId, operationLog);
        zeebeAPI.monitorBackup(backupId, operationLog);

        // Finish? Then stop all restoration pod
        operationLog.operationStep("Resume Zeebe");
        zeebeAPI.resumeExporting(operationLog);

        operationLog.endOperation();

        jobStatus = JOBSTATUS.COMPLETED;
    }

    public enum JOBSTATUS {PLANNED, INPROGRESS, COMPLETED, FAILED}
}
