package com.jadmatar.restaurant.service;

import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;
import com.jadmatar.restaurant.repository.MenuItemRepository;

import java.math.BigDecimal;
import java.util.List;

public class MenuItemService{
    private final MenuItemRepository menuItemRepository;
    public MenuItemService(MenuItemRepository menuItemRepository){
        if(menuItemRepository==null)
        {
            throw new IllegalArgumentException("REPO IS NULL");
        }
        this.menuItemRepository=menuItemRepository;
    }
    public MenuItem createMenuItem(BigDecimal itemPrice,String itemName,MenuCategory itemCategory){
            MenuItem menuItem=new MenuItem(itemPrice,itemName,itemCategory);
            menuItemRepository.add(menuItem);
            return menuItem;
    }
    public MenuItem updateItemPrice(int id, BigDecimal newPrice){
        MenuItem menuItem=findItemById(id);
        if(menuItem==null) return null;
        menuItem.setItemPrice(newPrice);
        menuItemRepository.update(menuItem);
        return menuItem;
    }
    public MenuItem findItemById(int id)
    {
        return menuItemRepository.findById(id);
    }
    public List<MenuItem> getAllMenuItems(){
        return menuItemRepository.findAll();
    }
    public MenuItem updateItemName(int id, String newName){
        MenuItem item=findItemById(id);
        if(item==null) return null;
        item.setItemName(newName);
        menuItemRepository.update(item);
        return item;
    }
    public MenuItem updateItemCategory(int id, MenuCategory newCategory){
        MenuItem item=findItemById(id);
        if(item==null) return null;
        item.setItemCategory(newCategory);
        menuItemRepository.update(item);
        return item;
    }
    public MenuItem markItemAvailable(int id){
        MenuItem item=findItemById(id);
        if(item==null) return null;
        item.markAvailable();
        menuItemRepository.update(item);
        return item;    }
    public MenuItem markItemUnavailable(int id){
        MenuItem item=findItemById(id);
        if(item==null) return null;
        item.markUnavailable();
        menuItemRepository.update(item);
        return item;
    }

}
