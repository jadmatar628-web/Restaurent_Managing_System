package com.jadmatar.restaurant.service;

import com.jadmatar.restaurant.domain.Employee;
import com.jadmatar.restaurant.domain.EmployeePosition;
import com.jadmatar.restaurant.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.ArrayList;

public class EmployeeService {
    private EmployeeRepository employeeRepository;
    public EmployeeService(EmployeeRepository employeeRepository) {
        if (employeeRepository == null) {
            throw new IllegalArgumentException("Employee repository cannot be null");
        }
        this.employeeRepository = employeeRepository;
    }
    public Employee createEmployee(String name, String phoneNumber, LocalDate employmentDate, EmployeePosition position)
    {
        Employee newEmployee=new Employee(name,phoneNumber,employmentDate,position);
        employeeRepository.add(newEmployee);
        return newEmployee;
    }
    public Employee deactivateEmployee(int id)
    {
        Employee employee=employeeRepository.findById(id);
        if(employee==null) return null;
        employee.deactivate();
        return employee;
    }
    public Employee reactivateEmployee(int id)
    {
        Employee employee=employeeRepository.findById(id);
        if(employee==null) return null;
        employee.reactivate();
        return employee;
    }
    public Employee findEmployeeById(int id)
    {
        Employee employee=employeeRepository.findById(id);
        return employee;
    }
    public ArrayList<Employee> getAllEmployees()
    {
        return employeeRepository.findAll();
    }
    public Employee updateEmployeeName(int id, String newName)
    {
        Employee temp=employeeRepository.findById(id);
        if(temp == null) return null;
        temp.setName(newName);
        return temp;
    }
    public Employee updatePhoneNumber (int id, String newPhoneNumber){
        Employee employee=findEmployeeById(id);
        if(employee==null) return null;
        employee.setPhoneNumber(newPhoneNumber);
        return employee;
    }
    public Employee updateEmployeePosition(int id, EmployeePosition position){
        Employee employee=findEmployeeById(id);
        if(employee==null) return null;
        employee.setPosition(position);
        return employee;
    }

}

