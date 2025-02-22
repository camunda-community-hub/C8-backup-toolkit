package io.camunda.blueberry.client;

import java.time.LocalDateTime;

public class BackupInfo {

    public long backupId;
    public String backupName;
    /**
     * Time in UTC
     */
    public LocalDateTime backupTime;
    public Status status;
    public enum Status {SUCCESS, FAILURE, INPROGRESS}
}
