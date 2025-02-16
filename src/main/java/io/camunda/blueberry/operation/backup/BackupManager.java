package io.camunda.blueberry.operation.backup;


import org.rocksdb.BackupInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class are in charge to start a backup,
 */
@Component
public class BackupManager {

    public void startBackup(Long backupId)
    {
        // Verify first is there is not already a backup in progress

        // start a backup, asynchrously
    }

    public class BackupInfo{
        public String id;
        public Date dateBackup;
    }

    /**
     * Return the list of all backups visible on the platform
     * @return
     */
    public List<BackupInfo> getListBackup() {
        return Collections.emptyList();
    }

}
