package io.camunda.blueberry.checkup.rule;


import io.camunda.blueberry.config.BlueberryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Operate define a repository, and the repository exist in ElasticSearch
 */
@Component
public class RuleZeebeContainer implements Rule {


    @Autowired
    BlueberryConfig blueberryConfig;

    @Override
    public boolean validRule() {
        // is Operate is define in the cluster?
        return false;
    }

    @Override
    public String getName() {
        return "Zeebe Container";
    }

    public String getExplanations() {
        return "Zeebe must define a container to backup the data.";
    }

    @Override
    public RuleInfo check() {
        // get the Pod description
        RuleInfo ruleInfo = new RuleInfo(this);
        if (validRule()) {
            ruleInfo.status = RuleStatus.FAILED;
            ruleInfo.details = "Not implemented yet";
        } else
            ruleInfo.status = RuleStatus.DEACTIVATED;
        return ruleInfo;
    }
}
