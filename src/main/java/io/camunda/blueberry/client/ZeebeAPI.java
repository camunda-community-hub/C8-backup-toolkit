package io.camunda.blueberry.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ZeebeAPI {

    public void connection() {

    }

    public void backup(Long backupId) {

    }

    public void monitorBackup(Long backupId) {

    }

    public ZeebeInformation getInformation() {
        return new ZeebeInformation();
    }

    /**
     * https://docs.camunda.io/docs/8.7/self-managed/operational-guides/backup-restore/zeebe-backup-and-restore/#list-backups-api
     */

    public List<BackupInfo> getListBackup() {
        return Collections.emptyList();
    }

    public class ZeebeInformation {
        public int clusterSize;
        public int numberOfPartitions;
        public int replicaFactor;
    }

}
