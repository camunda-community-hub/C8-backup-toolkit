package io.camunda.blueberry.api;

import io.camunda.blueberry.client.BackupInfo;
import io.camunda.blueberry.client.ZeebeAPI;
import io.camunda.blueberry.exception.BackupException;
import io.camunda.blueberry.exception.OperationException;
import io.camunda.blueberry.operation.backup.BackupJob;
import io.camunda.blueberry.operation.backup.BackupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("blueberry")


public class BackupRestController {

    private final ZeebeAPI zeebeAPI;
    private final BackupManager backupManager;
    Logger logger = LoggerFactory.getLogger(BackupRestController.class);

    public BackupRestController(ZeebeAPI zeebeAPI, BackupManager backupManager) {
        this.zeebeAPI = zeebeAPI;
        this.backupManager = backupManager;
    }

    @PostMapping(value = "/api/backup/start", produces = "application/json")
    public Map<String, Object> startBackup(@RequestParam(required = false, name = "backupId") Long backupId) {
        // Return the backupId if no one was given
        logger.info("Rest [/api/backup/start] backupId[{}]", backupId);
        try {
            backupManager.startBackup(backupId);
        } catch (BackupException e) {
            // if a backup is already in progress, will return a status "ALREADYRUNNING"
            return Map.of("status", "BACKUP_CANT_START",
                    "explanation", e.getInformation() ,
                    "technicalExplanation", e.getDetailInformation(),

                    "backupId", e.getBackupId());
        } catch (OperationException e) {
            return Map.of("error", e.getExplanation());
        }
        // The backup is started asynchronously
        BackupJob backupJob = backupManager.getBackupJob();
        Map<String, Object> status = new HashMap<>();
        status.put("status", backupJob == null ? "STARTED" : backupJob.getJobStatus().toString());
        return status;
    }

    @GetMapping(value = "/api/backup/monitor", produces = "application/json")
    public Map<String, Object> monitorBackup() {
        BackupJob backupJob = backupManager.getBackupJob();
        Map<String, Object> status = new HashMap<>();
        status.put("status", backupJob == null ? "" : backupJob.getJobStatus().toString());
        return status;
    }


    /**
     * Return the list of existing Backup
     *
     * @return
     */
    @GetMapping(value = "/api/backup/list", produces = "application/json")
    public List<Map<String, Object>> listBackup(@RequestParam(name = "timezoneoffset") Long timezoneOffset) {

        try {
            logger.debug("Rest [/api/backup/list]");
            List<BackupInfo> listBackup = zeebeAPI.getListBackup();

            logger.info("Rest [/api/backup/list] found {} backups", listBackup.size());

            return listBackup.stream().map(obj -> {
                        Map<String, Object> record = new HashMap<>();
                        record.put("backupId", obj.backupId);
                        record.put("backupName", obj.backupName);
                        record.put("backupTime", DateOperation.dateTimeToHumanString(obj.backupTime, timezoneOffset));
                        record.put("backupStatus", obj.status == null ? "" : obj.status.toString());

                        return record;
                    })
                    .collect(Collectors.toList());
        } catch (OperationException e) {
            throw new RuntimeException(e);
        }
    }
}
