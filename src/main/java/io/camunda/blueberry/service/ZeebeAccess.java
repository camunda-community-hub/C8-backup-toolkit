package io.camunda.blueberry.service;

public class ZeebeAccess {

    public class ZeebeInformation{
        public int clusterSize;
        public int partitions;
        public int replicationFactor;
    }

    public ZeebeInformation getInformation();
}
