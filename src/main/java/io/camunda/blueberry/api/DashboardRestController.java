package io.camunda.blueberry.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.camunda.blueberry.access.ZeebeAccess;
import io.camunda.blueberry.config.ExplorationCluster;
import io.camunda.blueberry.platform.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("blueberry")

public class DashboardRestController {
    private final ExplorationCluster explorationCluster;
    Logger logger = LoggerFactory.getLogger(DashboardRestController.class);

    public DashboardRestController(ExplorationCluster explorationCluster) {
        this.explorationCluster = explorationCluster;
    }

    @GetMapping(value = "/api/dashboard/all", produces = "application/json")
    public DashboardStatus all(@RequestParam(name = "forceRefresh", required = false, defaultValue = "false") boolean forceRefresh) {
        try {logger.debug("Rest [/api/dashboard/all]");
            DashboardStatus status = new DashboardStatus();

            if (forceRefresh) {
                // Force the refresh
                explorationCluster.refresh();
            }
            ZeebeAccess.ClusterInformation clusterInformation = explorationCluster.getClusterInformation();

            if (clusterInformation != null) {
                status.cluster.partitionsCount = clusterInformation.partitionsCount;
                status.cluster.replicationfactor = clusterInformation.replicationFactor;
                status.cluster.clusterSize = clusterInformation.clusterSize;

            }
            Boolean exporterStatus = explorationCluster.getExporterStatus();
            status.cluster.statusCluster = Boolean.TRUE.equals(exporterStatus) ? "ACTIF" : Boolean.FALSE.equals(exporterStatus) ? "DISABLED" : "";

            status.backup.backups = "";
            status.backup.step = "";
            status.backup.statusBackup = "ACTIF";

            status.scheduler.statusScheduler = "INACTIF";
            status.scheduler.cron = "";
            status.scheduler.next = "";
            status.scheduler.delay = "";

            Rule.RuleStatus ruleStatus = explorationCluster.rulesOk();
            status.configuration.statusConfiguration = ruleStatus == null ? "" : ruleStatus.toString();

            return status;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class DashboardStatus {
        @JsonProperty
        ClusterInfo cluster = new ClusterInfo();
        @JsonProperty
        BackupInfo backup = new BackupInfo();
        @JsonProperty
        SchedulerInfo scheduler = new SchedulerInfo();
        @JsonProperty
        DashboardRestController.ConfigurationInfo configuration = new DashboardRestController.ConfigurationInfo();
    }

    private class ClusterInfo {
        @JsonProperty
        Integer partitionsCount;
        @JsonProperty
        Integer replicationfactor;
        @JsonProperty
        Integer clusterSize;
        @JsonProperty
        String statusCluster; // "ACTIVE"
    }

    private class BackupInfo {
        @JsonProperty
        List<String> history = new ArrayList<>();
        @JsonProperty
        String statusBackup;
        @JsonProperty
        String step;
        @JsonProperty
        String backups;
    }

    private class SchedulerInfo {
        @JsonProperty
        String statusScheduler; // "INACTIF",
        @JsonProperty
        String cron;
        @JsonProperty
        String next;
        @JsonProperty
        String delay;
    }

    private class ConfigurationInfo {

        @JsonProperty
        String statusConfiguration;

    }
}