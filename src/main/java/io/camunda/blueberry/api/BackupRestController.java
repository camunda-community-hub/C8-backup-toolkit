package io.camunda.blueberry.api;

import io.camunda.blueberry.access.BackupInfo;
import io.camunda.blueberry.access.ZeebeAccess;
import io.camunda.blueberry.exception.OperationException;
import io.camunda.blueberry.operation.backup.BackupJob;
import io.camunda.blueberry.operation.backup.BackupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("blueberry")


public class BackupRestController {

    private final ZeebeAccess zeebeAccess;
    private final BackupManager backupManager;
    Logger logger = LoggerFactory.getLogger(BackupRestController.class);

    public BackupRestController(ZeebeAccess zeebeAccess, BackupManager backupManager) {
        this.zeebeAccess = zeebeAccess;
        this.backupManager = backupManager;
    }


    @PostMapping(value = "/api/backup/start", produces = "application/json")
    public Map<String, Object> startBackup(@RequestBody BackupManager.BackupParameter backupParameter) {
        // Return the backupId if no one was given

        logger.info("Rest [/api/backup/start] nextId[{}] backupId[{}]", backupParameter.nextId, backupParameter.backupId);
        try {
            backupManager.startBackup(backupParameter);
        } catch (OperationException e) {
            return e.getRecord();
        }
        // The backup is started asynchronously
        BackupJob backupJob = backupManager.getBackupJob();
        Map<String, Object> status = new HashMap<>();
        status.put("statusOperation", backupJob == null ? "STARTED" : backupJob.getJobStatus().toString());
        status.put("backupId", backupJob == null ? -1 : backupJob.getBackupId());

        return status;
    }

    @GetMapping(value = "/api/backup/monitor", produces = "application/json")
    public Map<String, Object> monitorBackup() {
        BackupJob backupJob = backupManager.getBackupJob();
        Map<String, Object> status = new HashMap<>();
        status.put("statusOperation", backupJob == null ? "" : backupJob.getJobStatus().toString());
        return status;
    }


    /**
     * Return the list of existing Backup
     *
     * @return
     */
    @GetMapping(value = "/api/backup/list", produces = "application/json")
    public Map<String, Object> listBackup(@RequestParam(name = "timezoneoffset") Long timezoneOffset) {

        Map<String, Object> result = new HashMap<>();
        try {
            logger.debug("Rest [/api/backup/list]");
            List<BackupInfo> listBackup = zeebeAccess.getListBackup();

            logger.info("Rest [/api/backup/list] found {} backups", listBackup.size());

            result.put("listBackup", listBackup.stream().map(obj -> {
                        Map<String, Object> mapRecord = new HashMap<>();
                        mapRecord.put("backupId", obj.backupId);
                        mapRecord.put("backupName", obj.backupName);
                        mapRecord.put("backupTime", DateOperation.dateTimeToHumanString(obj.backupTime, timezoneOffset));
                        mapRecord.put("backupStatus", obj.status == null ? "" : obj.status.toString());

                        return mapRecord;
                    })
                    .toList());
            return result;
        } catch (OperationException e) {
            result.putAll(e.getRecord());
            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
