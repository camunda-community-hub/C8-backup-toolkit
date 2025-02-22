package io.camunda.blueberry.exception;

public class BackupStartException extends OperationException {

    private final long backupId;

    public BackupStartException(String information, Long backupId) {
        super("BackupStartException", "Start error " + information);
        this.backupId = backupId;
    }

    public long getBackupId() {
        return backupId;
    }
}