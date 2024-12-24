package com.ntloc.demo.customer;

import com.ntloc.demo.AbstractTestContainersTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainersTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.create(
                "Ahmet",
                "ahmtatar@gmail.com",
                "Groove St."
        );
        customerRepository.save(testCustomer);
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldReturnCustomerWhenFindByEmail() {
        Optional<Customer> retrievedCustomer = customerRepository.findByEmail("ahmtatar@gmail.com");

        assertTrue(retrievedCustomer.isPresent(), "Retrieved customer should be present");
        Customer actualCustomer = retrievedCustomer.get();
        assertNotNull(actualCustomer, "Retrieved customer should not be null");
        assertEquals(testCustomer.getEmail(), actualCustomer.getEmail(), "Emails should match");
        assertEquals(testCustomer.getName(), actualCustomer.getName(), "Names should match");
        assertEquals(testCustomer.getAddress(), actualCustomer.getAddress(), "Addresses should match");
    }

    @Test
    void shouldNotFindCustomerWhenEmailIsNotPresent() {
        Optional<Customer> customerByEmail = customerRepository.findByEmail("somethingelse@gmail.com");

        assertFalse(customerByEmail.isPresent(), "Customer with the given email should not be present");
    }
}
