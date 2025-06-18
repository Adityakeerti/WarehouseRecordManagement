package com.recursiveMind.WareHouseRecordManagement.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.recursiveMind.WareHouseRecordManagement.repository.user",
        entityManagerFactoryRef = "userEntityManagerFactory",
        transactionManagerRef = "userTransactionManager"
)
@EntityScan(basePackages = "com.recursiveMind.WareHouseRecordManagement.model")
public class UserJpaRepositoriesConfig {
} 