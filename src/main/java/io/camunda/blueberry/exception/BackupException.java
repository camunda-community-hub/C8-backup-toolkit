package io.camunda.blueberry.exception;

import io.camunda.blueberry.client.CamundaApplication;
import io.camunda.blueberry.client.toolbox.WebActuator;

public class BackupException extends OperationException {

    private final long backupId;
    private final String information;
    private final String detailInformation;
    private final CamundaApplication.COMPONENT component;

    public BackupException(CamundaApplication.COMPONENT component, String information, String detailInformation, Long backupId) {
        super("BackupException", "Start error " + information);
        this.component = component;
        this.information=information;
        this.detailInformation=detailInformation;
        this.backupId = backupId;
    }

    public long getBackupId() {
        return backupId;
    }

    public String getInformation() {
        return information;
    }

    public String getDetailInformation() {
        return detailInformation;
    }

    public CamundaApplication.COMPONENT getComponent() {
        return component;
    }
}