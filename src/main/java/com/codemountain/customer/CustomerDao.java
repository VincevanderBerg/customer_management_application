package com.codemountain.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer customerId);
    void insertCustomer(Customer customer);
    boolean existsCustomerWithEmail(String email);
    boolean existsCustomerWithId(Integer customerId);
    void deleteCustomerWithId(Integer customerId);
    void updateCustomer(Customer updatedCustomer);
}
