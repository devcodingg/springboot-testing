package com.ntloc.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public abstract class AbstractTestContainersTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16.2");

    @Test
    void canEstablishDatabaseContainerConnection() {
        assertTrue(postgreSQLContainer.isCreated(), "Database container should be created");
        assertTrue(postgreSQLContainer.isRunning(), "Database container should be running");
    }

}
