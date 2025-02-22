package io.camunda.blueberry.operation.backup;


import io.camunda.blueberry.exception.BackupStartException;
import io.camunda.blueberry.exception.OperationException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class are in charge to start a backup,
 */
@Component
public class BackupManager {

    private BackupJob backupJob;

    public void startBackup(Long backupId) throws OperationException {
        // Verify first is there is not already a backup in progress
        if (backupJob != null && backupJob.getJobStatus() != BackupJob.JOBSTATUS.COMPLETED)
            throw new BackupStartException("Job already in progress[" + backupJob.getBackupId() + "]", backupJob.getBackupId());
        // start a backup, asynchrously

    }

    /**
     * Return the list of all backups visible on the platform
     *
     * @return
     */
    public List<BackupInfo> getListBackup() {
        return Collections.emptyList();
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

    public class BackupInfo {
        public String id;
        public Date dateBackup;
    }
}
