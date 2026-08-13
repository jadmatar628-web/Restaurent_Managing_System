package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.domain.Employee;

import java.util.ArrayList;

public interface EmployeeRepository {

    void add(Employee employee);

    Employee findById(int id);

    ArrayList<Employee> findAll();
}