package io.camunda.blueberry.client;

import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.operation.OperationLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiples component (Operate, TaskList, Optimize) react as the same way. To Simplify the management, they are mark as a "component"
 */
public interface CamundaApplication {
    enum COMPONENT { TASKLIST, OPERATE, OPTIMIZE, ZEEBERECORD}

    boolean exist();

    BackupOperation backup(Long backupId, OperationLog operationLog) throws BackupException;

    void waitBackup(Long backupId, OperationLog operationLog) throws BackupException;

    public class BackupOperation {
        public List<String> listSnapshots = new ArrayList<>();
        public int status;
        public String information;
        public String detailInformation;

        public boolean isOk() {
            return status == 200 || status == 202; // Accept both 200 (Operate, Tasklist) and 202 (Optimize)
        }
    }
}
