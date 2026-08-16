package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.Employee;
import com.jadmatar.restaurant.domain.EmployeePosition;

import java.sql.*;
import java.util.ArrayList;

public class JdbcEmployeeRepository implements EmployeeRepository {
    @Override
    public void add(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be NULL");
        }
        String sql = """
                INSERT INTO EMPLOYEE(PHONE_NUMBER,NAME,EMPLOYMENT_DATE,POSITION,IS_ACTIVE)
                VALUES(?,?,?,?,?)
                """;
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, employee.getPhoneNumber());
            statement.setString(2, employee.getName());
            statement.setDate(3, Date.valueOf(employee.getEmploymentDate()));
            statement.setString(4, employee.getPosition().name());
            statement.setBoolean(5, employee.isActive());
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Expected to insert one employee, but inserted " + affectedRows);
            }
            try (ResultSet generatedKey = statement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    int generatedId = generatedKey.getInt(1);
                    employee.assignId(generatedId);
                } else {
                    throw new RuntimeException("Employee was inserted but no generated ID was returned");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add employee", e);
        }
    }

    @Override
    public Employee findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        String sql = """
                SELECT * FROM EMPLOYEE WHERE EMPLOYEE_ID=?""";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Employee(
                            resultSet.getInt("EMPLOYEE_ID"),
                            resultSet.getString("NAME"),
                            resultSet.getString("PHONE_NUMBER"),
                            resultSet.getDate("EMPLOYMENT_DATE").toLocalDate(),
                            EmployeePosition.valueOf(resultSet.getString("POSITION")), resultSet.getBoolean("IS_ACTIVE"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't locate employee with ID    " + id, e);
        }
    }

    @Override
    public ArrayList<Employee> findAll() {
        ArrayList<Employee> employees = new ArrayList<>();
        String sql = """
                SELECT EMPLOYEE_ID,
                        NAME,
                        PHONE_NUMBER,
                        EMPLOYMENT_DATE,
                        POSITION,
                        IS_ACTIVE
                       FROM EMPLOYEE
                       ORDER BY EMPLOYEE_ID""";
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(new Employee(resultSet.getInt("EMPLOYEE_ID"),
                            resultSet.getString("NAME"),
                            resultSet.getString("PHONE_NUMBER"),
                            resultSet.getDate("EMPLOYMENT_DATE").toLocalDate(),
                            EmployeePosition.valueOf(resultSet.getString("POSITION")), resultSet.getBoolean("IS_ACTIVE")));
                }
                return employees;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't list all employees", e);
        }
    }
}
