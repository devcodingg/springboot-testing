package com.ntloc.demo.customer;

import com.ntloc.demo.AbstractTestcontainersTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainersTest {

    @Autowired
    CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        Customer customer = Customer.create(
                "leon",
                "leon@gmail.com",
                "US");
        underTest.save(customer);
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldReturnCustomerWhenFindByEmail() {
        //given
        //when
        Optional<Customer> customerByEmail = underTest.findByEmail("leon@gmail.com");
        //then
        assertThat(customerByEmail).isPresent();
    }

    @Test
    void shouldNotFoundCustomerWhenFindByEmailIsNotPresent() {
        //given
        //when
        Optional<Customer> customerByEmail = underTest.findByEmail("jason@gmail.com");
        //then
        assertThat(customerByEmail).isNotPresent();
    }
}