package com.codemountain.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
This class serves as the Data Access Layer
of the application.
 */

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    // Our fake customer database
    static List<Customer> customers;

    static {
        customers = new ArrayList<Customer>();

        Customer alex = new Customer(
                1,
                "Alex",
                "alex@gmail.com",
                28
        );
        customers.add(alex);

        Customer jamila = new Customer(
                2,
                "Jamila",
                "jamila@gmail.com",
                19
        );
        customers.add(jamila);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers
                .stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream()
                .anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public boolean existsCustomerWithId(Integer customerId) {
        return customers.stream()
                .anyMatch(customer -> customer.getId().equals(customerId));
    }

    @Override
    public void deleteCustomerWithId(Integer customerId) {
        customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        customers.stream()
                .filter(customer -> customer.getId().equals(updatedCustomer.getId()))
                .findFirst()
                .ifPresent(customers::remove);

        customers.add(updatedCustomer);

    }
}
