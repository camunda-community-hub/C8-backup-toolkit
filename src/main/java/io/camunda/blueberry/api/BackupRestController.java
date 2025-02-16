package io.camunda.blueberry.api;

import org.springframework.web.bind.annotation.*;

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
        return Map.of("backup", 1L);
    }

    @GetMapping(value = "/api/backup/monitor", produces = "application/json")
    public Map<String, Object> monitorBackup(@RequestParam(name = "backupId") Long backupId) {
        return Map.of("backup", 1L);
    }

    /**
     * Return the list of existing Backup
     *
     * @return
     */
    @GetMapping(value = "/api/backup/list", produces = "application/json")
    public List<Map<String, Object>> listBackup() {
        return Collections.emptyList();
    }
}
