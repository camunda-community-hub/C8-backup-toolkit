package io.camunda.blueberry.operation.backup;


import io.camunda.blueberry.client.*;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.exception.OperationException;
import io.camunda.blueberry.operation.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class are in charge to start a backup,
 */
@Component
public class BackupManager {

    private BackupJob backupJob;

    @Autowired
    OperateAPI operateAPI;
    @Autowired
    TaskListAPI taskListAPI;
    @Autowired
    OptimizeAPI optimizeAPI;
    @Autowired
    ZeebeAPI zeebeAPI;

    public synchronized void startBackup(Long backupId) throws OperationException {
        // Verify first is there is not already a backup in progress
        if (backupJob != null && backupJob.getJobStatus() != BackupJob.JOBSTATUS.COMPLETED)
            throw new BackupException(null, "Job already in progress[" + backupJob.getBackupId() + "]", backupJob.getBackupId());
        // start a backup, asynchrously
        backupJob = new BackupJob(operateAPI, taskListAPI, optimizeAPI, zeebeAPI, new OperationLog());
        if (backupId==null) {
            // calculate a new backup ID
            long maxId = 0;
            try {
                List<BackupInfo> listBackup = getListBackup();
                for (BackupInfo info : listBackup) {
                    if (info.backupId > maxId)
                        maxId = info.backupId;
                }
            }catch(OperationException e) {
                throw new BackupException(null, "Error getting list of backups: "+e.getExplanation(), backupId);
            }
            backupId=maxId+1;
        }
        backupJob.backup(backupId);
    }

    /**
     * Return the list of all backups visible on the platform
     *
     * @return
     */
    public List<BackupInfo> getListBackup() throws OperationException {
        return zeebeAPI.getListBackup();
    }

    /**
     * If a job is started, then a backupJob exist.
     * If the backup is terminated, then the backypJob is still available, with a status "TERMINATED"
     *
     * @return
     */
    public BackupJob getBackupJob() {
        return backupJob;
    }


}
