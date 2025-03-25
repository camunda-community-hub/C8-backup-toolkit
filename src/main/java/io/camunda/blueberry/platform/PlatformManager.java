package io.camunda.blueberry.platform;

import io.camunda.blueberry.platform.rule.Rule;
import io.camunda.blueberry.access.KubernetesAccess;
import io.camunda.blueberry.exception.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlatformManager {


    List<Rule> listRules;


    KubernetesAccess kubernetesAccess;


    @Autowired
    public PlatformManager(List<Rule> rules, KubernetesAccess kubernetesAccess)
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
    public List<Rule.RuleInfo> configure() throws OperationException {

        // Check the connection
        if (! kubernetesAccess.isConnected())
            kubernetesAccess.connection();

        return listRules.stream()
                .map(t -> t.configure())
                .toList();
    }
}
