package io.camunda.blueberry.api;


import io.camunda.blueberry.checkup.CheckupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("blueberry")

public class CheckupRestController {

    @Autowired
    private CheckupManager checkupManager;
    /**
     * Check the system
     * Does Zeebe declare a container? A Type storage?
     * Does Elasticsearch as repository for each component like OperateAPI?
     * @return
     */
    @GetMapping(value = "/api/checkup/check", produces = "application/json")
    public Map<String,Object> checkup() {
        return checkupManager.checkup();

    }

}
