package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.Employee;

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
        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1,employee.getPhoneNumber());
            statement.setString(2,employee.getName());
            statement.setDate(3,Date.valueOf(employee.getEmploymentDate()));
            statement.setString(4,employee.getPosition().name());
            statement.setBoolean(5,employee.isActive());
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Expected to insert one employee, but inserted " + affectedRows);
            }
            try(ResultSet generatedKey=statement.getGeneratedKeys()){
                if(generatedKey.next()){
                    int generatedId=generatedKey.getInt(1);
                    employee.assignId(generatedId);
                }
                else{
                    throw new RuntimeException("Employee was inserted but no generated ID was returned");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add employee", e);
        }
    }

    @Override
    public Employee findById(int id) {
        return null;
    }

    @Override
    public ArrayList<Employee> findAll() {
        return null;
    }
}
