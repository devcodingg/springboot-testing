package com.ntloc.demo.customer;

import com.ntloc.demo.AbstractTestContainersTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerIntegrationTest extends AbstractTestContainersTest {

    private static final String CUSTOMER_API_PATH = "/api/v1/customers";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldCreateCustomer() {
        // Arrange
        CreateCustomerRequest request = new CreateCustomerRequest(
                "John Doe",
                "john.doe" + UUID.randomUUID() + "@gmail.com",
                "123 Main St"
        );

        // Act
        ResponseEntity<Void> response = testRestTemplate.exchange(
                CUSTOMER_API_PATH,
                HttpMethod.POST,
                new HttpEntity<>(request),
                Void.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Customer creation should return HTTP 201");
    }

    @Test
    void shouldGetAllCustomers() {
        // Arrange
        createTestCustomer("Alice");
        createTestCustomer("Bob");

        // Act
        ResponseEntity<Customer[]> response = testRestTemplate.exchange(
                CUSTOMER_API_PATH,
                HttpMethod.GET,
                null,
                Customer[].class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Get all customers should return HTTP 200");
        assertNotNull(response.getBody(), "Customer list should not be null");
        assertTrue(response.getBody().length >= 2, "Customer list should contain at least two customers");
    }

    @Test
    void shouldGetCustomerById() {
        // Arrange
        Long customerId = createAndGetCustomerId("Charlie");

        // Act
        ResponseEntity<Customer> response = testRestTemplate.exchange(
                CUSTOMER_API_PATH + "/" + customerId,
                HttpMethod.GET,
                null,
                Customer.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Get customer by ID should return HTTP 200");
        assertNotNull(response.getBody(), "Customer should not be null");
        assertEquals("Charlie", response.getBody().getName(), "Customer name should match");
    }

    @Test
    void shouldUpdateCustomer() {
        // Arrange
        Long customerId = createAndGetCustomerId("David");
        String newName = "David Updated";
        String newAddress = "New Address";

        // Act
        ResponseEntity<Void> response = testRestTemplate.exchange(
                CUSTOMER_API_PATH + "/" + customerId + "?name=" + newName + "&address=" + newAddress,
                HttpMethod.PUT,
                null,
                Void.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Update customer should return HTTP 204");

        ResponseEntity<Customer> updatedCustomerResponse = testRestTemplate.exchange(
                CUSTOMER_API_PATH + "/" + customerId,
                HttpMethod.GET,
                null,
                Customer.class
        );

        assertNotNull(updatedCustomerResponse.getBody(), "Updated customer should not be null");
        assertEquals(newName, updatedCustomerResponse.getBody().getName(), "Customer name should be updated");
        assertEquals(newAddress, updatedCustomerResponse.getBody().getAddress(), "Customer address should be updated");
    }

    @Test
    void shouldDeleteCustomer() {
        // Arrange
        Long customerId = createAndGetCustomerId("Eve");

        // Act
        ResponseEntity<Void> response = testRestTemplate.exchange(
                CUSTOMER_API_PATH + "/" + customerId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Delete customer should return HTTP 204");

        ResponseEntity<Customer> deletedCustomerResponse = testRestTemplate.exchange(
                CUSTOMER_API_PATH + "/" + customerId,
                HttpMethod.GET,
                null,
                Customer.class
        );

        assertEquals(HttpStatus.NOT_FOUND, deletedCustomerResponse.getStatusCode(), "Deleted customer should not be found");
    }

    // Helper methods
    private void createTestCustomer(String name) {
        CreateCustomerRequest request = new CreateCustomerRequest(
                name,
                name.toLowerCase() + UUID.randomUUID() + "@example.com",
                "Test Address"
        );
        ResponseEntity<Void> response = testRestTemplate.postForEntity(CUSTOMER_API_PATH, request, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Customer creation should return HTTP 201");
    }

    private Long createAndGetCustomerId(String name) {
        createTestCustomer(name);

        ResponseEntity<Customer[]> getAllResponse = testRestTemplate.exchange(
                CUSTOMER_API_PATH,
                HttpMethod.GET,
                null,
                Customer[].class
        );

        assertNotNull(getAllResponse.getBody(), "Customer list should not be null");
        assertTrue(getAllResponse.getBody().length > 0, "Customer list should not be empty");

        return getAllResponse.getBody()[getAllResponse.getBody().length - 1].getId();
    }
}
