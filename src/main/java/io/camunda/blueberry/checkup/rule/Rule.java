package io.camunda.blueberry.checkup.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Rule
 */
public interface Rule {

    boolean validRule();

    String getName();

    /* Explain what the rule is doing, what it check */
    String getExplanations();

    List<String> getUrlDocumentation();

    /* Check the rule */
    RuleInfo check();

    /**
     * Configure : run the configuration to create the environnement to run backups
     *
     * @return
     */
    RuleInfo configure();


    enum RuleStatus {DEACTIVATED, CORRECT, FAILED, INPROGRESS}


    /**
     * A rule is executed, and this is the information returned for this execution
     */
    class RuleInfo {
        private boolean valid;
        private StringBuffer details = new StringBuffer();
        private RuleStatus status = RuleStatus.INPROGRESS;
        private final Rule rule;

        public record Tuple(String action, RuleStatus actionStatus) {
        }

        private List<Tuple> listVerifications = new ArrayList();

        public RuleInfo(Rule rule) {
            this.rule = rule;
        }

        public String getName() {
            return rule.getName();
        }


        public void addDetails(String details) {
            this.details.append(details + ";");
        }


        public String getDetails() {
            if (status == RuleStatus.DEACTIVATED) {
                return "The component is deactivated, check is not necessary.";
            }
            return details.toString();
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public RuleStatus getStatus() {
            return status;
        }

        public boolean inProgress() {
            return status == RuleStatus.INPROGRESS;
        }

        public void setStatus(RuleStatus status) {
            this.status = status;
        }

        public List<Tuple> getListVerifications() {
            return listVerifications;
        }

        public void addListVerifications(String action, RuleStatus actionStatus) {
            this.listVerifications.add(new Tuple(action, actionStatus));
        }


        public Rule getRule() {
            return this.rule;
        }
    }
}