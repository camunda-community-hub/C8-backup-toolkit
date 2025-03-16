package io.camunda.blueberry.checkup.rule;

/**
 * Rule
 */
public interface Rule {

    boolean validRule();

    String getName();

    /* Explain what the rule is doing, what it check */
    String getExplanations();

    /* Check the rule */
    RuleInfo check();

    enum RuleStatus {DEACTIVATED, CORRECT, FAILED}

    class RuleInfo {
        public boolean valid;
        public String details;
        public RuleStatus status;
        private final Rule rule;

        public RuleInfo(Rule rule) {
            this.rule = rule;
        }

        public String getName() {
            return rule.getName();
        }

        public String getDetails() {
            if (status == RuleStatus.DEACTIVATED) {
                return "The component is deactivated, check is not necessary.";
            }
            return details == null ? "" : details;
        }

        public Rule getRule() {
            return this.rule;
        }

    }
}