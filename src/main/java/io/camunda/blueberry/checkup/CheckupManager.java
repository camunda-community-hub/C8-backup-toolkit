package io.camunda.blueberry.checkup;

import io.camunda.blueberry.checkup.rule.Rule;
import io.camunda.blueberry.client.KubernetesAccess;
import io.camunda.blueberry.exception.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckupManager {


    List<Rule> listRules;


    KubernetesAccess kubernetesAccess;


    @Autowired
    public CheckupManager(List<Rule> rules, KubernetesAccess kubernetesAccess)
    {
        this.listRules = rules;
        this.kubernetesAccess = kubernetesAccess;
    }


    /**
     * Check the system
     * Does Zeebe declare a container? A Type storage?
     * Does Elasticsearch as repository for each component like OperateAccess?
     *
     * @return
     */
    public List<Rule.RuleInfo> checkAllRules() throws OperationException {

        // Check the connection
        if (! kubernetesAccess.isConnected())
            kubernetesAccess.connection();

        return listRules.stream()
                .map(t -> t.check())
                .toList();
    }

}
