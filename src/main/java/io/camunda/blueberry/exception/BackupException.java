package io.camunda.blueberry.exception;

import io.camunda.blueberry.client.WebActuator;

public class BackupException extends OperationException {

    private final long backupId;
    private final WebActuator.COMPONENT component;

    public BackupException(WebActuator.COMPONENT component, String information, Long backupId) {
        super("BackupException", "Start error " + information);
        this.component = component;
        this.backupId = backupId;
    }

    public long getBackupId() {
        return backupId;
    }

    public WebActuator.COMPONENT getComponent() {
        return component;
    }
}