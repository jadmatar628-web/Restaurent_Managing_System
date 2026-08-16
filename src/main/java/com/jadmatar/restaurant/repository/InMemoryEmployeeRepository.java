package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.domain.Employee;

import java.util.ArrayList;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private ArrayList<Employee> employees = new ArrayList<>(); //creating a new ArrayList (a dynamic array) that only accepts Employee (our predefined enum class) and using employees as reference to this array
private int nextId=1;
    @Override
    public void add(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }

        // Employee already has an ID, such as test setup data
        if (employee.getId() != null) {
            for (Employee existing : employees) {
                if (existing.getId().equals(employee.getId())) {
                    throw new IllegalArgumentException(
                            "Employee ID already exists"
                    );
                }
            }

            if (employee.getId() >= nextId) {
                nextId = employee.getId() + 1;
            }
        }
        // Newly created employee with no ID
        else {
            employee.assignId(nextId);
            nextId++;
        }

        employees.add(employee);
    }

    public Employee findById(Integer id) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == id) {
                return employees.get(i);
            }
        }
        return null;
    }

    public ArrayList<Employee> findAll() {
        return new ArrayList<>(employees);
    }

}
