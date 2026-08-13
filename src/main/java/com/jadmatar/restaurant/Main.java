package com.jadmatar.restaurant;

import com.jadmatar.restaurant.repository.InMemoryEmployeeRepository;
import com.jadmatar.restaurant.repository.MenuItemRepository;
import com.jadmatar.restaurant.service.EmployeeService;
import com.jadmatar.restaurant.service.MenuItemService;
import com.jadmatar.restaurant.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        InMemoryEmployeeRepository repository=new InMemoryEmployeeRepository();
        MenuItemRepository menuItemRepository=new MenuItemRepository();
        EmployeeService service=new EmployeeService(repository);
        MenuItemService menuItemService= new MenuItemService(menuItemRepository);
        ConsoleMenu menu=new ConsoleMenu(service,menuItemService);
        menu.start();
    }
}