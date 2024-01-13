package com.codemountain.journey;

import com.codemountain.customer.Customer;
import com.codemountain.customer.CustomerRegistrationRequest;
import com.codemountain.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static final Faker FAKER = new Faker();
    public static final String CUSTOMER_URI = "api/v1/customers";

    @Test
    void canRegisterNewCustomer() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();

        int age = 20;
        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest(name, email, age);

        // Send post request
        webTestClient
                .post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Send get request for all customers
        List<Customer> allCustomers = webTestClient
                .get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        final int expectedId = allCustomers
                .stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        final Customer expectedCustomer = new Customer(expectedId, name, email, age);

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        // Get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(customer.getId()).isEqualTo(expectedCustomer.getId());
                    assertThat(customer.getName()).isEqualTo(expectedCustomer.getName());
                    assertThat(customer.getEmail()).isEqualTo(expectedCustomer.getEmail());
                    assertThat(customer.getAge()).isEqualTo(expectedCustomer.getAge());
                });
    }

    @Test
    void canDeleteCustomer() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        int age = 20;

        CustomerRegistrationRequest request =
                new CustomerRegistrationRequest(name, email, age);

        // Send post request
        webTestClient
                .post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Send get request for all customers
        List<Customer> allCustomers = webTestClient
                .get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        final int expectedId = allCustomers
                .stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // Send delete request
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // Send get request for customer by id
        webTestClient
                .get()
                .uri(CUSTOMER_URI + "/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // Given
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        int age = 20;

        CustomerRegistrationRequest request
                = new CustomerRegistrationRequest(name, email, age);

        String updatedName = FAKER.name().fullName();
        String updatedEmail = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        int updatedAge = 30;

        CustomerUpdateRequest updateRequest
                = new CustomerUpdateRequest(updatedName, updatedEmail, updatedAge);


        // Send post request
        webTestClient
                .post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Send get request for all customers
        List<Customer> allCustomers = webTestClient
                .get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        final int expectedId = allCustomers
                .stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // Send put request
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Send get request for customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .value(customer -> {
                    assertThat(customer.getId()).isEqualTo(expectedId);
                    assertThat(customer.getName()).isEqualTo(updatedName);
                    assertThat(customer.getEmail()).isEqualTo(updatedEmail);
                    assertThat(customer.getAge()).isEqualTo(updatedAge);
                });
    }
}
