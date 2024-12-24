package com.ntloc.demo.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CustomerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.2"));

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void canEstablishDatabaseContainerConnection() {
        assertTrue(postgreSQLContainer.isCreated());
        assertTrue(postgreSQLContainer.isRunning());
    }

    @Test
    void shouldReturnCustomerWhenFindByEmail() {
        Customer customer = Customer.create(
                "Ahmet",
                "ahmtatar@gmail.com",
                "Groove St."
        );
        customerRepository.save(customer);

        Optional<Customer> retrievedCustomer = customerRepository.findByEmail("ahmtatar@gmail.com");

        assertTrue(retrievedCustomer.isPresent(), "Retrieved customer should be present");
        Customer actualCustomer = retrievedCustomer.get();
        assertNotNull(actualCustomer, "Retrieved customer should not be null");
        assertEquals(customer.getEmail(), actualCustomer.getEmail(), "Emails should match");
        assertEquals(customer.getName(), actualCustomer.getName(), "Names should match");
        assertEquals(customer.getAddress(), actualCustomer.getAddress(), "Addresses should match");
    }
}