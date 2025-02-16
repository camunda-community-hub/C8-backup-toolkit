package io.camunda.blueberry.operation.backup;

import io.camunda.blueberry.client.ElasticsearchAPI;
import io.camunda.blueberry.client.OperateClient;
import io.camunda.blueberry.client.OptimizeAPI;
import io.camunda.blueberry.client.TaskListAPI;
import io.camunda.blueberry.operation.OperationLog;
import io.camunda.zeebe.client.ZeebeClient;

/**
 * Collect all operation of the backup. This is returned to the monitoring, or at the end of the execution
 */
public class BackupJob {

    private OperateClient operateClient;
    private TaskListAPI taskListAPI;
    private OptimizeAPI optimizeClient;
    private ElasticsearchAPI elasticsearchAPI;
    private ZeebeClient zeebeClient;

    protected BackupJob(OperateClient operateClient,
                        TaskListAPI taskListAPI,
                        OptimizeAPI optimizeClient,
                        ElasticsearchAPI elasticsearchAPI,
                        ZeebeClient zeebeClient) {
        this.operateClient = operateClient;
        this.taskListAPI = taskListAPI;
        this.optimizeClient = optimizeClient;
        this.elasticsearchAPI = elasticsearchAPI;
        this.zeebeClient = zeebeClient;
    }

    OperationLog operationLog;

    public OperationLog getStatus() {
        return operationLog;
    }

    public void backup() {
        long beginTime = System.currentTimeMillis();
        operationLog.info("Start Backup");

        // backup Operate

        // backup TaskList
        if (taskListAPI.isTaskListExist()) {
            // Srart the backup
        }
        // backup Optimize


        // Wait end of backup Operate, TaskList, Optimize, Zeebe

        // Stop Zeebe imported


        // backup Zeebe record
        // backup Zeebe

        // Finish? Then stop all restoration pod

        // scale up Zeebe

        operationLog.info("End of backup in " + (System.currentTimeMillis() - beginTime) + " ms");

    }
}
