package com.ntloc.demo.customer;

import com.ntloc.demo.exception.CustomerEmailUnavailableException;
import com.ntloc.demo.exception.CustomerNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new CustomerNotFoundException("Customer with id " + id + " doesn't found"));
    }

    public void createCustomer(CreateCustomerRequest createCustomerRequest) {
        Optional<Customer> customerByEmail = customerRepository.findByEmail(createCustomerRequest.email());
        if (customerByEmail.isPresent()) {
            throw new CustomerEmailUnavailableException("The email " + createCustomerRequest.email() + " unavailable.");
        }
        Customer customer = Customer.create(createCustomerRequest.name(),
                createCustomerRequest.email(),
                createCustomerRequest.address());
        customerRepository.save(customer);
    }


    public void updateCustomer(Long id, String name, String email, String address) {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new CustomerNotFoundException("Customer with id " + id + " doesn't found"));
        if (Objects.nonNull(name) && !Objects.equals(customer.getName(), name)) {

            customer.setName(name);
        }
        if (Objects.nonNull(email) && !Objects.equals(customer.getEmail(), email)) {
            Optional<Customer> customerByEmail = customerRepository.findByEmail(email);
            if (customerByEmail.isPresent()) {
                throw new CustomerEmailUnavailableException("The email \"" + email + "\" unavailable to update");
            }
            customer.setEmail(email);
        }
        if (Objects.nonNull(address) && !Objects.equals(customer.getAddress(), address)) {

            customer.setAddress(address);
        }

        customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        boolean isExist = customerRepository.existsById(id);
        if (!isExist) {
            throw new CustomerNotFoundException("Customer with id " + id + " doesn't exist.");
        }
        customerRepository.deleteById(id);
    }


}
