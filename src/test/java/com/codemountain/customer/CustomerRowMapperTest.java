package com.codemountain.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {

    @Mock
    ResultSet resultSet;
    CustomerRowMapper underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerRowMapper();
    }

    @Test
    void testMapRow() throws SQLException {
        // Given
        when(resultSet.getInt("id")).thenReturn(42);
        when(resultSet.getString("name")).thenReturn("Foo");
        when(resultSet.getString("email")).thenReturn("fUQp2@example.com");
        when(resultSet.getInt("age")).thenReturn(20);

        // When
        Customer actual = underTest.mapRow(resultSet, 1);

        // Then
        assertThat(actual)
                .hasFieldOrPropertyWithValue("id", 42)
                .hasFieldOrPropertyWithValue("name", "Foo")
                .hasFieldOrPropertyWithValue("email", "fUQp2@example.com")
                .hasFieldOrPropertyWithValue("age", 20);
    }
}
