package com.example.activiti_demo.config;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.*;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author hebulin
 */
@Configuration
@Slf4j
public class ActivitiBeanConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Bean
    @Primary
    public ProcessEngine processEngine() {
        log.info("加载工作流引擎...");

        SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
        springProcessEngineConfiguration.setDataSource(dataSource);
        log.info("数据源已设置");

        springProcessEngineConfiguration.setTransactionManager(dataSourceTransactionManager);
        log.info("数据源事务管理器：打开");

        // 现在有表了 就不检查表了 false为不检查  记得比较数据库中act_ge_property 中 schema.version 所存储的版本
        springProcessEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        log.info("数据库结构更新检查：打开");

        springProcessEngineConfiguration.setAsyncExecutorActivate(false);
        log.info("异步激活：关闭");

        ProcessEngine processEngine = springProcessEngineConfiguration.buildProcessEngine();
        log.info("工作流引擎加载完成");

        return processEngine;
    }

    @Bean
    public RepositoryService getRepositoryService() {
        return processEngine().getRepositoryService();
    }

    @Bean
    public RuntimeService getRuntimeService() {
        return processEngine().getRuntimeService();
    }

    @Bean
    public TaskService getTaskService() {
        return processEngine().getTaskService();
    }

    @Bean
    public HistoryService getHistoryService() {
        return processEngine().getHistoryService();
    }
}
