package io.camunda.blueberry.checkup.rule;


import io.camunda.blueberry.config.BlueberryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public List<String> getUrlDocumentation() {
        return List.of();
    }


    @Override
    public RuleInfo check() {
        return operation(false);
    }

    @Override
    public RuleInfo configure() {
        return operation(true);
    }

    private RuleInfo operation(boolean execute) {

        // get the Pod description
        RuleInfo ruleInfo = new RuleInfo(this);
        if (validRule()) {
            ruleInfo.setStatus( RuleStatus.FAILED);
            ruleInfo.addDetails("Not implemented yet");
        } else
            ruleInfo.setStatus( RuleStatus.DEACTIVATED);
        return ruleInfo;
    }
}
