package com.codemountain.customer;

import com.codemountain.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
    }

    @Test
    void existsCustomerByEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 23);

        underTest.save(customer);

        // When
        boolean actual = underTest.existsCustomerByEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailNotPresent() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 23);

        underTest.save(customer);

        // When
        boolean actual = underTest.existsCustomerByEmail("some.wrong@email.com");

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 23);

        underTest.save(customer);

        Integer id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        boolean actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 23);

        underTest.save(customer);

        Integer id = -1;

        // When
        boolean actual = underTest.existsCustomerById(id);

        // Then
        assertThat(actual).isFalse();
    }
}
