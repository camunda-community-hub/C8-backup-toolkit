package io.camunda.blueberry.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
/**
 * Manage communication to OperateAPI
 */
public class OperateAPI {
    public void connection() {

    }

    public boolean isOperateExist() {
        return true;
    }

    public void backup(Long backupId) {

    }

    public void monitorBackup(Long backupId) {

    }

    /**
     * According to the documentation, Operate has a API to get all backup
     * https://docs.camunda.io/docs/8.7/self-managed/operational-guides/backup-restore/operate-tasklist-backup/#get-backups-list-api
     */
    public List<BackupInfo> getListBackup() {
        return Collections.emptyList();
    }


}
