package com.jadmatar.restaurant.domain;

import java.time.LocalDate;

public class Employee {
    private final LocalDate employmentDate;
    private EmployeePosition position;
    private Integer id;
    private boolean isActive; //true for active false for inactive
    private String name;
    private String phoneNumber;

    public Employee(String name, String phoneNumber, LocalDate employmentDate, EmployeePosition position) {
        this.isActive = true;
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid Name Entry");
        }
        this.name = name;
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Invalid Phone Number Entry");
        }
        this.phoneNumber = phoneNumber;

        LocalDate maxAllowedDate = LocalDate.now().plusDays(30);
        if (employmentDate == null || employmentDate.isAfter(maxAllowedDate)) {
            throw new IllegalArgumentException("Invalid Date Entry");
        }
        this.employmentDate = employmentDate;
        if (position == null) {
            throw new IllegalArgumentException("Position can't be NULL");
        }
        this.position = position;
        this.id = null;
    }

    public Employee(int id, String name, String phoneNumber, LocalDate employmentDate, EmployeePosition position) {
        if (id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID MUST BE GREATER THAN 0");
        }

        this.isActive = true;

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid Name Entry");
        }
        this.name = name;
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Invalid Phone Number Entry");
        }
        this.phoneNumber = phoneNumber;

        LocalDate maxAllowedDate = LocalDate.now().plusDays(30);
        if (employmentDate == null || employmentDate.isAfter(maxAllowedDate)) {
            throw new IllegalArgumentException("Invalid Date Entry");
        }
        this.employmentDate = employmentDate;
        if (position == null) {
            throw new IllegalArgumentException("Position can't be NULL");
        }
        this.position = position;
    }
    public Employee(int id, String name, String phoneNumber, LocalDate employmentDate, EmployeePosition position, Boolean isActive) {
        if (id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID MUST BE GREATER THAN 0");
        }
        if(isActive==null){
            throw new IllegalArgumentException("Activity status cannot be null");
        }
        this.isActive = isActive;

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Invalid Name Entry");
        }
        this.name = name;
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Invalid Phone Number Entry");
        }
        this.phoneNumber = phoneNumber;

        LocalDate maxAllowedDate = LocalDate.now().plusDays(30);
        if (employmentDate == null || employmentDate.isAfter(maxAllowedDate)) {
            throw new IllegalArgumentException("Invalid Date Entry");
        }
        this.employmentDate = employmentDate;
        if (position == null) {
            throw new IllegalArgumentException("Position can't be NULL");
        }
        this.position = position;
    }


    public Integer getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public EmployeePosition getPosition() {
        return position;
    }

    public void setPosition(EmployeePosition position) {
        if (position == null) throw new IllegalArgumentException("Position can't be NULL");
        this.position = position;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank())
            this.name = name;
        else throw new IllegalArgumentException("Invalid Name Entry");
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Invalid Phone Number Entry");
        }
        this.phoneNumber = phoneNumber;
    }

    public void deactivate() {
        isActive = false;
    }

    public void reactivate() {
        isActive = true;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", employmentDate=" + employmentDate +
                ", position=" + position +
                ", active=" + isActive +
                '}';
    }

    public void assignId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (this.id != null) {
            throw new IllegalStateException("Employee already has an ID");
        }
        this.id = id;
    }

}

