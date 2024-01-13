package com.codemountain.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper rowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        final String sqlQuery = """
                SELECT id, name, email, age
                FROM customer;
                """;

        return jdbcTemplate.query(sqlQuery, rowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        final String sqlQuery = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = (?)
                """;

        return jdbcTemplate.query(sqlQuery, rowMapper, customerId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sqlQuery = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;

        int result = jdbcTemplate.update(
                sqlQuery,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        System.out.println("jdbcTemplate.update = " + result);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        final String sqlQuery = """
                SELECT exists(SELECT 1 FROM customer WHERE email = (?))
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, email));
    }

    @Override
    public boolean existsCustomerWithId(Integer customerId) {
        final String sqlQuery = """
                SELECT exists(SELECT 1 FROM customer WHERE id = (?))
                """;

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class, customerId));
    }

    @Override
    public void deleteCustomerWithId(Integer customerId) {
        final String sqlQuery = """
                DELETE FROM customer
                WHERE id = (?)
                """;

        jdbcTemplate.update(sqlQuery, customerId);
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        final String sqlQuery = """
                UPDATE customer
                SET name = (?), email = (?), age = (?)
                WHERE id = (?)
                """;

        jdbcTemplate.update(
                sqlQuery,
                updatedCustomer.getName(),
                updatedCustomer.getEmail(),
                updatedCustomer.getAge(),
                updatedCustomer.getId()
        );
    }
}
