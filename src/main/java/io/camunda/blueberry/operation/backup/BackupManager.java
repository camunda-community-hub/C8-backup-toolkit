package io.camunda.blueberry.operation.backup;


import io.camunda.blueberry.component.Operate;
import org.springframework.stereotype.Component;

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


}
