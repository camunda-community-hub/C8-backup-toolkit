package io.camunda.blueberry.operation.backup;


import io.camunda.blueberry.api.BackupRestController;
import io.camunda.blueberry.client.*;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.exception.OperationException;
import io.camunda.blueberry.operation.OperationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class are in charge to start a backup,
 */
@Component
public class BackupManager {
    Logger logger = LoggerFactory.getLogger(BackupManager.class);

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
        if (backupJob != null && backupJob.getJobStatus() == BackupJob.JOBSTATUS.INPROGRESS)
            throw new BackupException(null, "Job already in progress[" + backupJob.getBackupId() + "]", "", backupJob.getBackupId());
        // start a backup, asynchrously
        backupJob = new BackupJob(operateAPI, taskListAPI, optimizeAPI, zeebeAPI, new OperationLog());
        if (backupId==null) {
            logger.info("No backup is provided, calculate the new Id");
            // calculate a new backup ID
            long maxId = 0;
            try {
                List<BackupInfo> listBackup = getListBackup();
                for (BackupInfo info : listBackup) {
                    if (info.backupId > maxId)
                        maxId = info.backupId;
                }

            }catch(OperationException e) {
                logger.error("Error when accessing the list of Backup: {}", e);
                throw new BackupException(null, "Error getting list of backups",e.getExplanation(), backupId);
            }
            backupId=maxId+1;
            logger.info("No backupId is provided, calculate from the list +1 : {}", backupId);
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
