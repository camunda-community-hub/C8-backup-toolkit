package io.camunda.blueberry.operation.backup;

import io.camunda.blueberry.access.*;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.operation.OperationLog;

import java.util.List;

/**
 * Collect all operation of the backup. This is returned to the monitoring, or at the end of the execution
 */
public class BackupJob {

    private final OperateAccess operateAccess;
    private final TaskListAccess taskListAccess;
    private final OptimizeAccess optimizeAccess;
    private final ZeebeAccess zeebeAccess;
    OperationLog operationLog;
    private JOBSTATUS jobStatus = JOBSTATUS.PLANNED;
    private long backupId;

    protected BackupJob(OperateAccess operateAccess, TaskListAccess taskListAccess, OptimizeAccess optimizeAccess,
                        ZeebeAccess zeebeAccess, OperationLog operationLog) {
        this.operateAccess = operateAccess;
        this.taskListAccess = taskListAccess;
        this.optimizeAccess = optimizeAccess;
        this.zeebeAccess = zeebeAccess;
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
        List<CamundaApplication> listApplications = List.of(operateAccess, taskListAccess, optimizeAccess)
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
        zeebeAccess.pauseExporting(operationLog);

        // create Zeebe repository
        operationLog.operationStep("Check Zeebe snapshot Repository Exists");
        zeebeAccess.ensureZeebeSnapshotExists(operationLog);

        // backup Zeebe record
        operationLog.operationStep("Backup Zeebe Elasticsearch");
        zeebeAccess.esBackup(backupId, operationLog);
//        zeebeAccess.monitorEsBackup(backupId, operationLog);

        // backup Zeebe
        operationLog.operationStep("Backup Zeebe");
        zeebeAccess.backup(backupId, operationLog);
//        zeebeAccess.monitorBackup(backupId, operationLog);

        // Finish? Then stop all restoration pod
        operationLog.operationStep("Resume Zeebe");
        zeebeAccess.resumeExporting(operationLog);

        operationLog.endOperation();

        jobStatus = JOBSTATUS.COMPLETED;
    }

    public enum JOBSTATUS {PLANNED, INPROGRESS, COMPLETED, FAILED}
}
