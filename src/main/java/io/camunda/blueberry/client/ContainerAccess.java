package io.camunda.blueberry.client;

/**
 * backup manipulate Container: ElasticSearch, Kubernetes, Docker, Saas.
 * This abstract class help the manipulation
 */
public abstract class ContainerAccess {

    abstract OperationResult connection();

    public class OperationResult {
        public boolean success;
        public String command;
        public String details;

        /**
         * if the result is a String, this contains it
         */
        public String resultSt;
        public boolean resultBoolean;

    }

}
