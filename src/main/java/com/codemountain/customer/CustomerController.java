package com.codemountain.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
This class is responsible for orchestrating
and managing interactions between the client
and the underlying application logic.
 */

@RestController
@RequestMapping(path = "api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{id}")
    public Customer getCustomerById(@PathVariable(name = "id") Integer customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public void registerCustomer(
            @RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
    }

    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable(name = "id") Integer customerId) {
        customerService.deleteCustomerById(customerId);
    }

    @PutMapping("{id}")
    public void updateCustomer(
            @PathVariable(name = "id") Integer customerId,
            @RequestBody CustomerUpdateRequest request) {
       customerService.updateCustomer(customerId, request);
    }
}
