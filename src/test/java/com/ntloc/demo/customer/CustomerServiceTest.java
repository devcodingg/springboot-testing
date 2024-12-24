package com.ntloc.demo.customer;

import com.ntloc.demo.exception.CustomerEmailUnavailableException;
import com.ntloc.demo.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.create(
                1L,
                "Leon",
                "leon@gmail.com",
                "US"
        );
    }

    @Test
    void shouldGetAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(testCustomer));

        List<Customer> customers = customerService.getCustomers();

        assertNotNull(customers, "The customer list should not be null");
        assertEquals(1, customers.size(), "There should be exactly one customer");
        assertEquals(testCustomer, customers.get(0), "The returned customer should match the test data");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldGetCustomerById() {
        long id = 1L;
        when(customerRepository.findById(id)).thenReturn(Optional.of(testCustomer));

        Customer customer = customerService.getCustomerById(id);

        assertNotNull(customer, "The customer should not be null");
        assertEquals(testCustomer, customer, "The returned customer should match the test data");
        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void shouldThrowNotFoundWhenCustomerIdIsInvalid() {
        long id = 5L;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(id));
        assertEquals("Customer with id " + id + " doesn't found", exception.getMessage());
        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void shouldCreateCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Leon",
                "leon@gmail.com",
                "US"
        );
        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        customerService.createCustomer(request);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertEquals(request.name(), capturedCustomer.getName(), "The customer name should match");
        assertEquals(request.email(), capturedCustomer.getEmail(), "The customer email should match");
        assertEquals(request.address(), capturedCustomer.getAddress(), "The customer address should match");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsUnavailable() {
        CreateCustomerRequest request = new CreateCustomerRequest(
                "Leon",
                "leon@gmail.com",
                "US"
        );
        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.of(testCustomer));

        Exception exception = assertThrows(CustomerEmailUnavailableException.class, () ->
                customerService.createCustomer(request));
        assertEquals("The email " + request.email() + " unavailable.", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        long id = 1L;
        String newName = "Leonardo";
        when(customerRepository.findById(id)).thenReturn(Optional.of(testCustomer));

        customerService.updateCustomer(id, newName, null, null);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();
        assertEquals(newName, capturedCustomer.getName(), "The customer name should be updated");
        assertEquals(testCustomer.getEmail(), capturedCustomer.getEmail(), "The customer email should remain the same");
        assertEquals(testCustomer.getAddress(), capturedCustomer.getAddress(), "The customer address should remain the same");
    }


    @Test
    void shouldThrowExceptionWhenUpdatingCustomerWithUnavailableEmail() {
        long id = 1L;
        String newEmail = "newemail@gmail.com";
        when(customerRepository.findById(id)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.of(new Customer()));

        Exception exception = assertThrows(CustomerEmailUnavailableException.class, () ->
                customerService.updateCustomer(id, null, newEmail, null));
        assertEquals("The email \"" + newEmail + "\" unavailable to update", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {
        long id = 1L;
        when(customerRepository.existsById(id)).thenReturn(true);

        customerService.deleteCustomer(id);

        verify(customerRepository, times(1)).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCustomer() {
        long id = 1L;
        when(customerRepository.existsById(id)).thenReturn(false);

        Exception exception = assertThrows(CustomerNotFoundException.class, () ->
                customerService.deleteCustomer(id));
        assertEquals("Customer with id " + id + " doesn't exist.", exception.getMessage());
        verify(customerRepository, never()).deleteById(any());
    }
}
