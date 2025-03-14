package io.camunda.blueberry.checkup;

import io.camunda.blueberry.checkup.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class CheckupManager {


    List<Rule> listRules;


    @Autowired
    public CheckupManager(List<Rule> rules) {
        this.listRules = rules;
    }


    /**
     * Check the system
     * Does Zeebe declare a container? A Type storage?
     * Does Elasticsearch as repository for each component like OperateAPI?
     *
     * @return
     */
    public List<Rule.RuleInfo> checkAllRules() {
        return listRules.stream()
                .map(t->t.check())
                .toList();
    }

}
