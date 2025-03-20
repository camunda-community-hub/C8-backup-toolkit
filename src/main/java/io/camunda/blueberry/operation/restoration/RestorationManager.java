package io.camunda.blueberry.operation.restoration;

import io.camunda.blueberry.client.ZeebeAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The restoration manager are in chage of one restoration
 */
@Component
public class RestorationManager {


    public RestorationJob currentRestorationJob;
    @Autowired
    ZeebeAccess zeebeAccess;

    public void startRestoration(Long backupId) {
        // Collect the parameters : number of cluster, partitions, replica factor

        ZeebeAccess.ZeebeInformation zeebeInformation = zeebeAccess.getInformation();
        // Do that in asynchronous : start the new thread to run it

        // start a RestorationJob, and keep it here
    }


}
