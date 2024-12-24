package com.ntloc.demo.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

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