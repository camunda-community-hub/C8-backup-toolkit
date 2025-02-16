package io.camunda.blueberry.operation.backup;

import io.camunda.blueberry.operation.OperationLog;

import java.util.Collections;
import java.util.Map;

/**
 * Collect all operation of the backup. This is returned to the monitoring, or at the end of the execution
 */
public class BackupJob {

    OperationLog operationLog;
    public OperationLog getStatus() {
        return operationLog;
    }

}
