package com.jadmatar.restaurant;

import com.jadmatar.restaurant.repository.EmployeeRepository;
import com.jadmatar.restaurant.repository.JdbcEmployeeRepository;
import com.jadmatar.restaurant.repository.InMemoryMenuItemRepository;
import com.jadmatar.restaurant.service.EmployeeService;
import com.jadmatar.restaurant.service.MenuItemService;
import com.jadmatar.restaurant.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        EmployeeRepository repository=new JdbcEmployeeRepository();
        InMemoryMenuItemRepository menuItemRepository=new InMemoryMenuItemRepository();
        EmployeeService service=new EmployeeService(repository);
        MenuItemService menuItemService= new MenuItemService(menuItemRepository);
        ConsoleMenu menu=new ConsoleMenu(service,menuItemService);
        menu.start();
    }
}