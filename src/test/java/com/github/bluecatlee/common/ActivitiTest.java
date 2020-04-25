package com.github.bluecatlee.common;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

/**
 * Created by 胶布 on 2020/4/25.
 */
public class ActivitiTest {

    @Test
    public void testAutoGenTables() {
        // 流程引擎配置
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("config/activiti.cfg.xml");
        // ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
        // ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();

        // 创建流程引擎对象
        ProcessEngine processEngine = configuration.buildProcessEngine();
        // ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        System.out.println(processEngine);

    }

}
