package com.jadmatar.restaurant.repository;

import com.jadmatar.restaurant.database.DatabaseConnection;
import com.jadmatar.restaurant.domain.MenuCategory;
import com.jadmatar.restaurant.domain.MenuItem;

import java.lang.classfile.instruction.ReturnInstruction;
import java.sql.*;
import java.util.List;

public class JdbcMenuItemRepository implements MenuItemRepository{
    @Override
    public void add(MenuItem menuItem) {
        if(menuItem==null){
            throw new IllegalArgumentException("Menu Item cannot be empty");
        }
        String sql= """
                INSERT INTO MENU_ITEM(ITEM_PRICE,ITEM_NAME,ITEM_CATEGORY) VALUES(?,?,?)""";

        try(Connection connection=DatabaseConnection.getConnection(); PreparedStatement statement=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            statement.setBigDecimal(1,menuItem.getItemPrice());
            statement.setString(2,menuItem.getItemName());
            statement.setString(3,menuItem.getItemCategory().name());
            int affectedRows=statement.executeUpdate();
            if(affectedRows==1){
                try(ResultSet generatedKey=statement.getGeneratedKeys()){
                    if(generatedKey.next()){
                        int generatedId=generatedKey.getInt(1);
                        menuItem.setId(generatedId);
                    }else{
                        throw new RuntimeException("Menu item was inserted but ID generation failed");
                    }
                }
            }else{
                throw new RuntimeException("Expected to affect 1 row but affected "+affectedRows+".");
            }
        }catch (SQLException e){
            throw new RuntimeException("Cannot create a new menu item.",e);
        }
    }

    @Override
    public MenuItem findById(int id) {
        if(id<=0){
         throw new IllegalArgumentException("Id cannot be <=0");
        }
        String sql= """
                SELECT MENU_ITEM_ID,ITEM_PRICE,ITEM_CATEGORY,ITEM_NAME,IS_AVAILABLE
                from MENU_ITEM
                where MENU_ITEM_ID=?
                """;
        try(Connection connection=DatabaseConnection.getConnection();PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setInt(1,id);
          try(ResultSet resultSet=statement.executeQuery()){
              if(resultSet.next()){
                  return new MenuItem(resultSet.getInt("MENU_ITEM_ID"),
                          resultSet.getBigDecimal("ITEM_PRICE"),
                          resultSet.getString("ITEM_NAME"),
                          MenuCategory.valueOf(resultSet.getString("MENU_CATEGORY")),
                          resultSet.getBoolean("IS_AVAILABLE")
                          );
              }
              return null;
          }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot find item.",e);
        }
    }

    @Override
    public List<MenuItem> findAll() {
        return List.of();
    }

    @Override
    public void update(MenuItem menuItem) {

    }
}
