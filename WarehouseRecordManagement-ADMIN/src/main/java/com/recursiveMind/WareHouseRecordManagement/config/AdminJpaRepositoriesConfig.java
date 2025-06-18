package com.recursiveMind.WareHouseRecordManagement.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.recursiveMind.WareHouseRecordManagement.repository", // <--- Only this, NOT .repository.user
        excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(
                type = org.springframework.context.annotation.FilterType.REGEX,
                pattern = "com\\.recursiveMind\\.WareHouseRecordManagement\\.repository\\.user\\..*"
        ),
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@EntityScan(basePackages = "com.recursiveMind.WareHouseRecordManagement.model")
public class AdminJpaRepositoriesConfig {
}