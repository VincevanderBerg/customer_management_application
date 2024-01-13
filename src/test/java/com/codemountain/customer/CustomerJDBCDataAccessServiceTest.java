package com.codemountain.customer;

import com.codemountain.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper rowMapper = new CustomerRowMapper();

    @BeforeEach
    public void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                rowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insertCustomer(customer);

        // When
        List<Customer> actual = underTest.selectAllCustomers();

        // Then
        assertThat(actual).isNotEmpty();
        assertThat(actual).hasExactlyElementsOfTypes(Customer.class);
    }

    @Test
    void selectCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
           assertThat(c.getId()).isEqualTo(id);
           assertThat(c.getName()).isEqualTo(customer.getName());
           assertThat(c.getEmail()).isEqualTo(customer.getEmail());
           assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerByWrongId() {
        // Given
        Integer id = -1;

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void existsCustomerWithEmail() {
        // Given
        final String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existsCustomerWithEmail(email);

        // Then
        assertThat(actual).isTrue();

    }

    @Test
    void existsCustomerWithEmailReturnsFalseWhenDoesNotExist() {
        // Given
        final String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsCustomerWithEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        // Given
        final String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existsCustomerWithId(1);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdReturnsFalseWhenDoesNotExist() {
        // Given
        Integer id = 0;

        // When
        boolean actual = underTest.existsCustomerWithId(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void insertCustomer() {
        // Given
        Customer newCustomer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + UUID.randomUUID(),
                20
        );

        // When
        underTest.insertCustomer(newCustomer);

        // Then
        boolean doesExist = underTest.existsCustomerWithEmail(newCustomer.getEmail());
        assertThat(doesExist).isTrue();
    }

    @Test
    void deleteCustomerWithId() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 20);
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.deleteCustomerWithId(id);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 55);

        underTest.insertCustomer(customer);

        final Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "foo";

        // When
        Customer update = underTest.selectCustomerById(id).orElseThrow();
        update.setName(newName);

        underTest.updateCustomer(update);


        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 55);

        underTest.insertCustomer(customer);

        final Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = "foo@mail.com";

        // When
        Customer update = underTest.selectCustomerById(id).orElseThrow();
        update.setEmail(newEmail);

        underTest.updateCustomer(update);


        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(FAKER.name().fullName(), email, 55);

        underTest.insertCustomer(customer);

        final Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Integer newAge = 21;

        // When
        Customer update = underTest.selectCustomerById(id).orElseThrow();
        update.setAge(newAge);

        underTest.updateCustomer(update);


        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }
}
