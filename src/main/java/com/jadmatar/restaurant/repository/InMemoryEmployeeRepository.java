package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.domain.Employee;

import java.util.ArrayList;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private ArrayList<Employee> employees = new ArrayList<>(); //creating a new ArrayList (a dynamic array) that only accepts Employee (our predefined enum class) and using employees as reference to this array

    public void add(Employee employee)//creating a public method called add that takes as parameter a new "employee" of type Employee
    {
        if (employee == null) //safety check
        {
            throw new IllegalArgumentException("NULL");
        }
        for (int i = 0; i < employees.size(); i++) {
            Employee exist = employees.get(i); //employees.get(i) return the whole object stored at index i, overwritten with every new i
            if (exist.getId() == employee.getId()) {
                throw new IllegalArgumentException("Employee is null or Employee ID already exists");
            }
        }
        employees.add(employee);//using the .add built in method that registers the employee in the arraylist
    }

    public Employee findById(int id) {
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
