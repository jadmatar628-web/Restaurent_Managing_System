package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.domain.Employee;

import java.util.ArrayList;

public interface EmployeeRepository {

    void add(Employee employee);

    Employee findById(Integer id);

    ArrayList<Employee> findAll();
    void update(Employee employee);
}
