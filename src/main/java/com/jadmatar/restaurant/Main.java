package com.jadmatar.restaurant;

import com.jadmatar.restaurant.repository.EmployeeRepository;
import com.jadmatar.restaurant.repository.JdbcEmployeeRepository;
import com.jadmatar.restaurant.repository.JdbcMenuItemRepository;
import com.jadmatar.restaurant.repository.JdbcOrderRepository;
import com.jadmatar.restaurant.repository.MenuItemRepository;
import com.jadmatar.restaurant.repository.OrderRepository;
import com.jadmatar.restaurant.service.EmployeeService;
import com.jadmatar.restaurant.service.MenuItemService;
import com.jadmatar.restaurant.service.OrderService;
import com.jadmatar.restaurant.ui.ConsoleMenu;

public class Main {

     static void main(String[] args) {

        EmployeeRepository employeeRepository =
                new JdbcEmployeeRepository();

        MenuItemRepository menuItemRepository =
                new JdbcMenuItemRepository();

        OrderRepository orderRepository =
                new JdbcOrderRepository();

        EmployeeService employeeService =
                new EmployeeService(
                        employeeRepository
                );

        MenuItemService menuItemService =
                new MenuItemService(
                        menuItemRepository
                );

        OrderService orderService =
                new OrderService(
                        orderRepository
                );

        ConsoleMenu menu =
                new ConsoleMenu(
                        employeeService,
                        menuItemService,
                        orderService
                );

        menu.start();
    }
}