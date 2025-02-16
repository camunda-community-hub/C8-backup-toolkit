package io.camunda.blueberry.component;

import io.camunda.blueberry.operation.restoration.RestorationManager;

public class Zeebe {
    public void connection() {

    }

    public void backup(Long backupId) {

    }

    public void monitorBackup(Long backupId) {

    }

    public void restore(Long backupId) {
        RestorationManager restorationManager = new RestorationManager(backupId);
        restorationManager.startRestoration();
    }

    public void monitorRestore(Long backupId) {

    }
}
