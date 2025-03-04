package io.camunda.blueberry.operation.restoration;

import io.camunda.blueberry.client.ZeebeAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The restoration manager are in chage of one restoration
 */
@Component
public class RestorationManager {


    @Autowired
    ZeebeAPI zeebeAccess;
    public RestorationJob currentRestorationJob;

    public void startRestoration(Long backupId) {
        // Collect the parameters : number of cluster, partitions, replica factor

        ZeebeAPI.ZeebeInformation zeebeInformation = zeebeAccess.getInformation();
        // Do that in asynchronous : start the new thread to run it

        // start a RestorationJob, and keep it here
    }


}
