package com.recursiveMind.WareHouseRecordManagement.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
// @EnableJpaRepositories(
//     basePackages = "com.recursiveMind.WareHouseRecordManagement.repository.admin",
//     entityManagerFactoryRef = "adminEntityManagerFactory",
//     transactionManagerRef = "adminTransactionManager"
// )
public class AdminJpaConfig {

    @Qualifier("adminEntityManagerFactory")
    private LocalContainerEntityManagerFactoryBean adminEntityManagerFactory;

    @Qualifier("adminTransactionManager")
    private PlatformTransactionManager adminTransactionManager;
} 