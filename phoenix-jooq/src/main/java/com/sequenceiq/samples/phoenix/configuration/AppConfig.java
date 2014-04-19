package com.sequenceiq.samples.phoenix.configuration;

import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.sequenceiq.samples.phoenix")
public class AppConfig {

    private static final String DRIVER_CLASS_NAME = "org.apache.phoenix.jdbc.PhoenixDriver";
    private static final String URL = "jdbc:phoenix:localhost:2181";

    @Bean(name = "dataSource")
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS_NAME);
        dataSource.setUrl(URL);
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager createDataSourceTransactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }

    @Bean
    public TransactionAwareDataSourceProxy dataSourceProxy() {
        TransactionAwareDataSourceProxy dataSourceProxy = new TransactionAwareDataSourceProxy();
        dataSourceProxy.setTargetDataSource(dataSource());
        return dataSourceProxy;
    }

    @Bean
    public DataSourceConnectionProvider createDataSourceConnectionProvider() {
        return new DataSourceConnectionProvider(dataSourceProxy());
    }

    @Bean
    public DefaultDSLContext createDefaultDSLContext() {
        return new DefaultDSLContext(dataSource(), SQLDialect.MYSQL);
    }
}
