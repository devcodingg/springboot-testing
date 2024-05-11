package com.ntloc.demo.customer;

import com.ntloc.demo.exception.CustomerEmailUnavailableException;
import com.ntloc.demo.exception.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    CustomerService underTest;
    @Mock
    CustomerRepository customerRepository;
    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerRepository);
    }

    @Test
    void shouldGetAllCustomers() {
        //given
        //when
        underTest.getCustomers();
        //then
        verify(customerRepository).findAll();
    }

    @Test
    void shouldThrowNotFoundWhenGivenInvalidIDWhileGetCustomerById() {
        //given
        long id = 5L;
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() ->
                underTest.getCustomerById(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id " + id + " doesn't found");
    }
    @Test
    void shouldGetCustomerById() {
        //given
        long id = 5L;
        String name = "leon";
        String email = "leon@gmail.com";
        String address = "US";
        Customer customer = Customer.create(
                id,
                name,
                email,
                address
        );
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        //when
        Customer customerById = underTest.getCustomerById(id);
        //then
        assertThat(customerById.getId()).isEqualTo(id);
        assertThat(customerById.getName()).isEqualTo(name);
        assertThat(customerById.getEmail()).isEqualTo(email);
        assertThat(customerById.getAddress()).isEqualTo(address);
    }


    @Test
    void shouldCreateCustomer() {
        //given
        CreateCustomerRequest createCustomerRequest =
                new CreateCustomerRequest(
                        "leon",
                        "leon@gmail.com",
                        "US");
        //when
        underTest.createCustomer(createCustomerRequest);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer customerCaptured = customerArgumentCaptor.getValue();

        assertThat(customerCaptured.getName()).isEqualTo(createCustomerRequest.name());
        assertThat(customerCaptured.getEmail()).isEqualTo(createCustomerRequest.email());
        assertThat(customerCaptured.getAddress()).isEqualTo(createCustomerRequest.address());

    }

    @Test
    void shouldNotCreateCustomerAndThrowExceptionWhenEmailUnavailable() {
        //given
        CreateCustomerRequest createCustomerRequest =
                new CreateCustomerRequest(
                        "leon",
                        "leon@gmail.com",
                        "US");
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(new Customer()));
        //when
        //then
        assertThatThrownBy(() ->
                underTest.createCustomer(createCustomerRequest))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessage("The email " + createCustomerRequest.email() + " unavailable.");

    }

    @Test
    void shouldThrowNotFoundWhenGivenInvalidIDWhileUpdateCustomer() {
        //given
        long id = 5L;
        String name = "leon";
        String email = "leon@gmail.com";
        String address = "US";
        when(customerRepository.findById(id))
                .thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() ->
                underTest.updateCustomer(id, name, email, address))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id " + id + " doesn't found");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldOnlyUpdateCustomerName() {
        //given
        long id = 5L;
        Customer customer = Customer.create(
                id,
                "leon",
                "leon@gmail.com",
                "US"
        );
        String newName = "leon mark";
        when(customerRepository.findById(id))
                .thenReturn(Optional.of(customer));
        //when
        underTest.updateCustomer(id, newName, null, null);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(newName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAddress()).isEqualTo(customer.getAddress());
    }

    @Test
    void shouldThrowEmailUnavailableWhenGivenEmailAlreadyPresentedWhileUpdateCustomer() {
        //given
        long id = 5L;
        Customer customer = Customer.create(
                id,
                "leon",
                "leon@gmail.com",
                "US"
        );
        String newEmail = "leonaldo@gmail.com";
        when(customerRepository.findById(id))
                .thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.of(new Customer()));
        //when
        //then
        assertThatThrownBy(() ->
                underTest.updateCustomer(id, null, newEmail, null))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessage("The email \"" + newEmail + "\" unavailable to update");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOnlyCustomerEmail() {
        //given
        long id = 5L;
        Customer customer = Customer.create(
                id,
                "leon",
                "leon@gmail.com",
                "US"
        );
        String newEmail = "leonaldo@gmail.com";
        when(customerRepository.findById(id))
                .thenReturn(Optional.of(customer));
        //when
        underTest.updateCustomer(id, null, newEmail, null);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAddress()).isEqualTo(customer.getAddress());
    }

    @Test
    void shouldUpdateOnlyCustomerAddress() {
        //given
        long id = 5L;
        Customer customer = Customer.create(
                id,
                "leon",
                "leon@gmail.com",
                "US"
        );
        String newAddress = "UK";
        when(customerRepository.findById(id))
                .thenReturn(Optional.of(customer));
        //when
        underTest.updateCustomer(id, null, null, newAddress);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
    }

    @Test
    void shouldUpdateAllAttributeWhenUpdateCustomer() {
        //given
        long id = 5L;
        Customer customer = Customer.create(
                id,
                "leon",
                "leon@gmail.com",
                "US"
        );
        String newName = "leonaldo";
        String newEmail = "leonaldo@gmail.com";
        String newAddress = "UK";
        when(customerRepository.findById(id))
                .thenReturn(Optional.of(customer));
        //when
        underTest.updateCustomer(id, newName, newEmail, newAddress);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(newName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
    }


    @Test
    void shouldThrowNotFoundWhenGivenIdDoesNotExistWhileDeleteCustomer() {
        //given
        long id = 5L;
        when(customerRepository.existsById(id))
                .thenReturn(false);
        //when
        //then
        assertThatThrownBy(() ->
                underTest.deleteCustomer(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id " + id + " doesn't exist.");
        verify(customerRepository, never()).deleteById(any());

    }

    @Test
    void shouldDeleteCustomer() {
        //given
        long id = 5L;
        when(customerRepository.existsById(id))
                .thenReturn(true);
        //when
        underTest.deleteCustomer(id);
        //then
        verify(customerRepository).deleteById(id);

    }


}