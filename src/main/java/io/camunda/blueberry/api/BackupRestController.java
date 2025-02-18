package io.camunda.blueberry.api;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("blueberry")


public class BackupRestController {


    @PutMapping(value = "/api/backup/start", produces = "application/json")
    public Map<String, Object> startBackup(@RequestParam(name = "backupId") Long backupId) {
        // Return the backupId if no one was given
        // The backup is started asynchronously

        // if a backup is already in progress, will return a status "ALREADYRUNNING"
        return Map.of("backupId", 1L, "status", "STARTED");
    }

    @GetMapping(value = "/api/backup/monitor", produces = "application/json")
    public Map<String, Object> monitorBackup(@RequestParam(name = "backupId") Long backupId) {
        return Map.of("backupId", 1L);
    }

    /**
     * Return the list of existing Backup
     *
     * @return
     */
    @GetMapping(value = "/api/backup/list", produces = "application/json")
    public List<Map<String, Object>> listBackup() {
        List<Map<String,Object>> listBackup = new ArrayList<>();
        listBackup.add(Map.of("backupId", 1L, "status", "OK", "backupDate", "2025-03-12"));
        return listBackup;
    }
}
