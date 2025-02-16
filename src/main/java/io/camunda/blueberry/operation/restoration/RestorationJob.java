package io.camunda.blueberry.operation.restoration;

public class RestorationJob {

    // Restore this backupId
    public RestorationJob(Long backupId) {

    }
    /** run asynchronously
     *
     */
    public void restoration() {
        // Scale down Zeebe

        // create one pod per cluster size, and start it

        // monitor each pod

        // Finish? Then stop all restoration pod

        // scale up Zeebe
    }
}
