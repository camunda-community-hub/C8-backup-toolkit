package io.camunda.blueberry.operation;

import io.camunda.blueberry.component.Operate;

public class OperationLog {

    private Long backupId;
    public OperationLog(Long backupId) {
        this.backupId = backupId;
    }

    public void run() {
        Operate operate = new Operate();
        operate.backup(backupId);


    }
}
