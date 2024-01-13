package com.codemountain.customer;

import com.codemountain.exception.DuplicateResourceException;
import com.codemountain.exception.RequestValidationException;
import com.codemountain.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/*
This class plays a crucial role in encapsulating
the underlying application logic and acts as an
intermediary between the Controller and the
Data Access Layer.
 */

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer customerId) {
        return customerDao
                .selectCustomerById(customerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Customer with id [%s] was not found.", customerId))
                );
    }

    public void addCustomer(
            CustomerRegistrationRequest customerRegistrationRequest) {
        // Check if email already exists
        final boolean customerExists = customerDao
                .existsCustomerWithEmail(customerRegistrationRequest.email());

        if (customerExists) {
            throw new DuplicateResourceException(
                    "Email already taken."
            );
        }

        // Create and add new customer
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer customerId) {
        // Check if customer exists
        final boolean customerExists = customerDao
                .existsCustomerWithId(customerId);

        if (!customerExists) {
            throw new ResourceNotFoundException("Customer with id [%s] was not found.".formatted(customerId));
        }

        customerDao.deleteCustomerWithId(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerUpdateRequest customerUpdateRequest) {
        final String name = customerUpdateRequest.name();
        final String email = customerUpdateRequest.email();
        final Integer age = customerUpdateRequest.age();

        final Customer existingCustomer = customerDao
                .selectCustomerById(customerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer with id [%s] was not found."
                                    .formatted(customerId))
                );

        boolean customerUpdated = false;

        if (!existingCustomer.getName().equals(name) && name != null) {
            existingCustomer.setName(name);
            customerUpdated = true;
        }

        if (!existingCustomer.getEmail().equals(email) && email != null) {
            if (customerDao.existsCustomerWithEmail(email)) {
                throw new DuplicateResourceException("Email already taken.");
            }
            existingCustomer.setEmail(email);
            customerUpdated = true;
        }

        if (!existingCustomer.getAge().equals(age) && age != null) {
            existingCustomer.setAge(age);
            customerUpdated = true;
        }

        if (!customerUpdated) {
            throw new RequestValidationException("No changes to customer data were detected.");
        }

        customerDao.updateCustomer(existingCustomer);
    }
}
