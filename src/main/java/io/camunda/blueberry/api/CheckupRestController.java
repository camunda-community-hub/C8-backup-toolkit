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

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.awt.SystemColor.info;

@RestController
@RequestMapping("blueberry")

public class CheckupRestController {

    Logger logger = LoggerFactory.getLogger(CheckupRestController.class);

    @Autowired
    private CheckupManager checkupManager;

    /**
     * Check the system
     * Does Zeebe declare a container? A Type storage?
     * Does Elasticsearch as repository for each component like OperateAPI?
     *
     * @return
     */
    @GetMapping(value = "/api/checkup/check", produces = "application/json")
    public List<Map<@NotNull String, Object>> check() {
        try {

            logger.debug("Rest [/api/checkup/check]");

            List<Rule.RuleInfo> listRules= checkupManager.checkAllRules();
            logger.info("End Rest [/api/checkup/check] {} rules", listRules.size());
return listRules.stream()
                    .sorted(Comparator.comparing(Rule.RuleInfo::getName)) // Sort by name
                    .map(ruleInfo -> {
                        Map<String, Object> info = Map.of("name", ruleInfo.getName(),
                                "valid", ruleInfo.valid,
                                "status", ruleInfo.status.toString(),
                                "detail", ruleInfo.getDetails(),
                                "explanations", ruleInfo.getRule().getExplanations());
                        return info;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
