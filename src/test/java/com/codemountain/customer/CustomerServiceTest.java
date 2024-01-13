package com.codemountain.customer;

import com.codemountain.exception.DuplicateResourceException;
import com.codemountain.exception.RequestValidationException;
import com.codemountain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Removes a lot of boilerplate code
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;
//    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
//        // Don't need to do this with MockitoExtension
//        autoCloseable = openMocks(this);
        underTest = new CustomerService(customerDao);
    }

    @AfterEach
    void tearDown() throws Exception {
//        // Don't need to do this with MockitoExtension
//        autoCloseable.close();
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCustomerWithGivenId() {
        // Given
        Integer id = 42;
        Customer customer = new Customer(
            id, "Foo", "fUQp2@example.com", 20
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void getCustomerWithWrongIdCanThrow() {
        // Given
        Integer id = 42;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] was not found.".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "fUQp2@example.com";
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
            "Foo", email, 20
        );

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor
                = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer)
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("name", "Foo")
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("age", 20);

    }

    @Test
    void addCustomerWillThrowIfEmailAlreadyExists() {
        // Given
        String email = "fUQp2@example.com";
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Foo", email, 20
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken.");

        // Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        // Given
        Integer id = 42;
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerWithId(id);
    }

    @Test
    void deleteCustomerWithWrongIdCanThrow() {
        // Given
        Integer id = 42;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] was not found.".formatted(id));

        verify(customerDao, never()).deleteCustomerWithId(id);
    }

    @Test
    void canUpdateAllCustomerProperties() {
        // Given
        Integer id = 42;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Foo", "foo@example.com", 20
        );
        Optional<Customer> customer = Optional.of(new Customer(id, "Bar", "bar@example.com", 24));
        when(customerDao.selectCustomerById(id)).thenReturn(customer);
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);

        // When
        underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor
                = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", request.name())
                .hasFieldOrPropertyWithValue("email", request.email())
                .hasFieldOrPropertyWithValue("age", request.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        Integer id = 42;
        CustomerUpdateRequest request = new CustomerUpdateRequest("Foo", null, null);
        Optional<Customer> customer = Optional.of(new Customer(id, "Bar", "bar@example.com", 24));
        when(customerDao.selectCustomerById(id)).thenReturn(customer);

        // When
        underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor
                = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", request.name())
                .hasFieldOrPropertyWithValue("email", customer.get().getEmail())
                .hasFieldOrPropertyWithValue("age", customer.get().getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        Integer id = 42;
        String email = "foo@example.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, email, null);
        Optional<Customer> customer = Optional.of(new Customer(id, "Bar", "bar@example.com", 24));
        when(customerDao.selectCustomerById(id)).thenReturn(customer);
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        // When
        underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor
                = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", customer.get().getName())
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("age", customer.get().getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        Integer id = 42;
        CustomerUpdateRequest request = new CustomerUpdateRequest(null, null, 99);
        Optional<Customer> customer = Optional.of(new Customer(id, "Bar", "bar@example.com", 24));
        when(customerDao.selectCustomerById(id)).thenReturn(customer);

        // When
        underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor
                = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", customer.get().getName())
                .hasFieldOrPropertyWithValue("email", customer.get().getEmail())
                .hasFieldOrPropertyWithValue("age", request.age());
    }






    @Test
    void updateCustomerWithWrongIdCanThrow() {
        // Given
        Integer id = 42;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Foo", "fUQp2@example.com", 20
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] was not found.".formatted(id));
    }

    @Test
    void updateCustomerWithIdenticalValuesCanThrow() {
        // Given
        Integer id = 42;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Foo", "fUQp2@example.com", 20
        );
        when(customerDao.selectCustomerById(id)).thenReturn(
                Optional.of(new Customer(id, "Foo", "fUQp2@example.com", 20))
        );

        // When
        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No changes to customer data were detected.");

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomerWithDuplicateEmailCanThrow() {
        // Given
        Integer id = 42;
        String email = "fUQp2@example.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Foo", email, 20
        );
        when(customerDao.selectCustomerById(id)).thenReturn(
                Optional.of(new Customer(id, "Foo", "anotherfUQp2@example.com", 20))
        );
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken.");

        verify(customerDao, never()).updateCustomer(any());
    }
}
