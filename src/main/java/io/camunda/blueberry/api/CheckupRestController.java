package io.camunda.blueberry.api;


import io.camunda.blueberry.checkup.CheckupManager;
import io.camunda.blueberry.checkup.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("blueberry")

public class CheckupRestController {

    Logger logger = LoggerFactory.getLogger(CheckupRestController.class);

    @Autowired
    private CheckupManager checkupManager;

    /**
     * Check the system
     * Does Zeebe declare a container? A Type storage?
     * Does Elasticsearch as repository for each component like OperateAccess?
     *
     * @return
     */
    @GetMapping(value = "/api/checkup/check", produces = "application/json")
    public List<Map<@NotNull String, Object>> check() {
        try {

            logger.debug("Rest [/api/checkup/check]");

            List<Rule.RuleInfo> listRules = checkupManager.checkAllRules();
            logger.info("End Rest [/api/checkup/check] {} rules", listRules.size());
            return listRules.stream()
                    .sorted(Comparator.comparing(Rule.RuleInfo::getName)) // Sort by name
                    .map(ruleInfo -> {
                        Map<String, Object> info = Map.of("name", ruleInfo.getName(),
                                "valid", ruleInfo.isValid(),
                                "status", ruleInfo.getStatus().toString(),
                                "detail", ruleInfo.getDetails(),
                                "explanations", ruleInfo.getRule().getExplanations(),
                                "urldocumentation", ruleInfo.getRule().getUrlDocumentation(),
                                "verifications", ruleInfo.getListVerifications()
                                        .stream()
                                        .map(tuple -> {
                                            return Map.of(
                                                    "action", tuple.action(),
                                                    "actionStatus", tuple.actionStatus().toString()
                                            );
                                        })
                                        .toList());
                        return info;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
