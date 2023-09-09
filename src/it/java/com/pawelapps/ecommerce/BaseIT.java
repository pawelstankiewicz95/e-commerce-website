package com.pawelapps.ecommerce;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest
public abstract class BaseIT {

    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0.33");

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeAll
    public static void setUpContainer(){
        mySQLContainer.withClasspathResourceMapping("sql/database-structure.sql", "/docker-entrypoint-initdb.d/init.sql", BindMode.READ_ONLY);
        mySQLContainer.start();
    }
}
