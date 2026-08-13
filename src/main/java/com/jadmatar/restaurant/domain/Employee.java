package com.jadmatar.restaurant.domain;

import java.time.LocalDate;

public class Employee {
    private EmployeePosition position;
    private int id;
    private boolean isActive; //true for active false for inactive
    private String name;
    private String phoneNumber;
    private LocalDate employmentDate;

    public Employee(int id, String name, String phoneNumber, LocalDate employmentDate, EmployeePosition position) {
        if (id > 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID MUST BE GREATER THAN 0");
        }

        this.isActive = true;

        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Invalid Name Entry");
        }
        this.name = name;
        if (phoneNumber == null || phoneNumber.isEmpty() || phoneNumber.isBlank()) {
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

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return isActive;
    }

    public EmployeePosition getPosition() {
        return position;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty() && !name.isBlank())
            this.name = name;
        else throw new IllegalArgumentException("Invalid Name Entry");
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank() || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Invalid Phone Number Entry");
        }
        this.phoneNumber = phoneNumber;
    }

    public void setPosition(EmployeePosition position) {
        if (position == null) throw new IllegalArgumentException("Position can't be NULL");
        this.position = position;
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

}

