package io.camunda.blueberry.checkup.rule;


import io.camunda.blueberry.config.BlueberryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Operate define a repository, and the repository exist in ElasticSearch
 */
@Component
public class RuleZeebeRecordRepository implements Rule {


    @Autowired
    BlueberryConfig blueberryConfig;

    @Override
    public boolean validRule() {
        // is a repository is define in the cluster?
        return true;
    }

    @Override
    public String getName() {
        return "Zeebe Record Repository";
    }

    public String getExplanations() {
        return "A repository must be defined in ElasticSearch to backup Zeebe Record data, and map it to a valid container.";
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
