package com.example.activiti_demo.config;

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
public class ActivitiBeanConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Bean
    @Primary
    public ProcessEngine processEngine() {
        SpringProcessEngineConfiguration springProcessEngineConfiguration = new SpringProcessEngineConfiguration();
        springProcessEngineConfiguration.setDataSource(dataSource);
        springProcessEngineConfiguration.setTransactionManager(dataSourceTransactionManager);
        // 现在有表了 就不检查表了 false为不检查  记得比较数据库中act_ge_property 中 schema.version 所存储的版本
        springProcessEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        springProcessEngineConfiguration.setAsyncExecutorActivate(false);
        return springProcessEngineConfiguration.buildProcessEngine();
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
