package io.camunda.blueberry.checkup.rule;


import io.camunda.blueberry.client.CamundaApplication;
import io.camunda.blueberry.client.ElasticSearchAccess;
import io.camunda.blueberry.client.KubernetesAccess;
import io.camunda.blueberry.config.BlueberryConfig;
import io.camunda.blueberry.exception.ElasticsearchException;
import io.camunda.blueberry.exception.KubernetesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Operate define a repository, and the repository exist in ElasticSearch
 */
@Component
public class RuleOperateRepository implements Rule {


    @Autowired
    BlueberryConfig blueberryConfig;

    @Autowired
    KubernetesAccess kubernetesAccess;

    @Autowired
    ElasticSearchAccess elasticSearchAccess;

    @Override
    public boolean validRule() {
        // is Operate is define in the cluster?
        return blueberryConfig.getOperateActuatorUrl() != null;
    }

    @Override
    public String getName() {
        return "Operate Repository";
    }

    public String getExplanations() {
        return "Operate must define a repository name. Elasticsearch must define this repository, and map it to a valid container.";
    }

    @Override
    public List<String> getUrlDocumentation() {
        return List.of("https://docs.camunda.io/docs/self-managed/operational-guides/backup-restore/operate-tasklist-backup/",
                "https://github.com/camunda-community-hub/C8-backup-toolkit/blob/main/README.md");
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
        boolean accessPodRepository = false;
        boolean accessElasticsearchRepository = false;
        boolean createElasticsearchRepository = false;


        // get the Pod description
        RuleInfo ruleInfo = new RuleInfo(this);

        if (validRule()) {
            // ---------- First step, ask Operate for the name of the repository
            // the rule is in progress
            ruleInfo.setStatus(RuleStatus.INPROGRESS);
            String operateRepository = null;
            try {
                operateRepository = kubernetesAccess.getRepositoryName(CamundaApplication.COMPONENT.OPERATE, blueberryConfig.getNamespace());
                if (operateRepository == null) {
                    ruleInfo.addDetails("Can't access the Repository name in the pod, or does not exist");
                    ruleInfo.setStatus(RuleStatus.FAILED);
                }

            } catch (KubernetesException e) {
                ruleInfo.setStatus(RuleStatus.FAILED);
                ruleInfo.addDetails(e.getMessage());
            }
            ruleInfo.addListVerifications("Access pod repository, retrieve [" + operateRepository + "]", ruleInfo.inProgress() ? RuleStatus.CORRECT : RuleStatus.FAILED);


            //------------ Second step, verify if the repository exist in elasticSearch
            if (ruleInfo.inProgress()) {
                // now check if the repository exist in Elastic search
                accessElasticsearchRepository = elasticSearchAccess.existRepository(operateRepository);
                ruleInfo.addListVerifications("Check Elasticsearch repository [" + operateRepository + "]",
                        accessElasticsearchRepository ? RuleStatus.CORRECT : RuleStatus.FAILED);

                // if the repository exist, then we stop the rule execution here
                if (accessElasticsearchRepository) {
                    ruleInfo.addDetails("Repository exist in Elastic search");
                    ruleInfo.setStatus(RuleStatus.CORRECT);
                } else {
                    // if we don't execute the rule, we stop here on a failure
                    if (!execute) {
                        ruleInfo.addDetails("Repository does not exist in Elastic search, and must be created");
                        ruleInfo.setStatus(RuleStatus.FAILED);
                    }
                }
            }


            // Third step, create the repository if asked
            if (execute && ruleInfo.inProgress()) {
                try {
                    elasticSearchAccess.createRepository(operateRepository,
                            blueberryConfig.getElasticsearchContainerType(),
                            blueberryConfig.getElasticsearchContainerName(),
                            blueberryConfig.getOperateContainerBasePath());
                    ruleInfo.addDetails("Repository is created in ElasticSearch");
                    ruleInfo.setStatus(RuleStatus.CORRECT);
                } catch (ElasticsearchException e) {
                    ruleInfo.addDetails("Error when creating the repository in ElasticSearch :" + e.getMessage());
                    ruleInfo.setStatus(RuleStatus.FAILED);
                }
                ruleInfo.addListVerifications("Check Elasticsearch repository [" + operateRepository
                                + "] ContainerType[" + blueberryConfig.getElasticsearchContainerType()
                                + "] ContainerName[" + blueberryConfig.getElasticsearchContainerName()
                                + "] basePath[" + blueberryConfig.getOperateContainerBasePath() + "]",
                        ruleInfo.getStatus());

            }
        }
        return ruleInfo;
    }
}
