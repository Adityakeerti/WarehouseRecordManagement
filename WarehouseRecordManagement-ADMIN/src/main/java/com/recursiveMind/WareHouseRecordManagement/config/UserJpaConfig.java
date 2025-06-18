package com.recursiveMind.WareHouseRecordManagement.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

@Configuration
public class UserJpaConfig {
    @Bean
    @Qualifier("userDataSourceProperties")
    public DataSourceProperties userDataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl("jdbc:mysql://localhost:3306/warehouse_user");
        properties.setUsername("your_user"); // <-- set your MySQL username
        properties.setPassword("your_password"); // <-- set your MySQL password
        return properties;
    }

    @Bean(name = "userDataSource")
    public DataSource userDataSource(@Qualifier("userDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "userEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("userDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.recursiveMind.WareHouseRecordManagement.model")
                .persistenceUnit("user")
                .build();
    }

    @Bean(name = "userTransactionManager")
    public JpaTransactionManager userTransactionManager(
            @Qualifier("userEntityManagerFactory") LocalContainerEntityManagerFactoryBean userEntityManagerFactory) {
        return new JpaTransactionManager(userEntityManagerFactory.getObject());
    }
} 