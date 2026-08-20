package com.jadmatar.restaurant.service;

import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;
import com.jadmatar.restaurant.repository.InMemoryMenuItemRepository;
import java.math.BigDecimal;
import java.util.ArrayList;

public class MenuItemService {
    private final InMemoryMenuItemRepository menuItemRepository;
    private int nextId=1;
    public MenuItemService(InMemoryMenuItemRepository menuItemRepository){
        if(menuItemRepository==null)
        {
            throw new IllegalArgumentException("REPO IS NULL");
        }
        this.menuItemRepository=menuItemRepository;
        ArrayList<MenuItem> menuItemArrayList=menuItemRepository.findAll();
        for(MenuItem menuItem:menuItemArrayList){
            if(menuItem.getId()>=nextId)
            {
                nextId=menuItem.getId()+1;
            }
        }
    }
    public MenuItem createMenuItem(BigDecimal itemPrice,String itemName,MenuCategory itemCategory){
            MenuItem menuItem=new MenuItem(nextId,itemPrice,itemName,itemCategory);
            menuItemRepository.addItem(menuItem);
            nextId++;
            return menuItem;
    }
    public MenuItem updateItemPrice(int id, BigDecimal newPrice){
        MenuItem menuItem=findItemById(id);
        if(menuItem==null) return null;
        menuItem.setItemPrice(newPrice);
        return menuItem;
    }
    public MenuItem findItemById(int id)
    {
        return menuItemRepository.findById(id);
    }
    public ArrayList<MenuItem> getAllMenuItems(){
        return menuItemRepository.findAll();
    }
    public MenuItem updateItemName(int id, String newName){
        MenuItem item=findItemById(id);
        if(item==null) return null;
        item.setItemName(newName);
        return item;
    }
    public MenuItem updateItemCategory(int id, MenuCategory newCategory){
        MenuItem item=findItemById(id);
        if(item==null) return null;
        item.setItemCategory(newCategory);
        return item;
    }
    public MenuItem markItemAvailable(int id){
        MenuItem item=findItemById(id);
        if(item==null) return null;
        item.markAvailable();
        return item;    }
    public MenuItem markItemUnavailable(int id){
        MenuItem item=findItemById(id);
        if(item==null) return null;
        item.markUnavailable();
        return item;
    }

}
