package com.jadmatar.restaurant.service;

import com.jadmatar.restaurant.domain.Order;
import com.jadmatar.restaurant.domain.OrderStatus;
import com.jadmatar.restaurant.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository){
        if(repository==null){
            throw new IllegalArgumentException("Constructor Argument cannot be NULL");
        }
        this.repository=repository;
    }
    public Order placeOrder(Order order){
        if(order==null)
        {
            throw new IllegalArgumentException("Order cannot be NULL");
        }
        order.markPlaced();
        return repository.add(order);
    }
    public Order findOrderById(int id){
        if(id<1){
            throw new IllegalArgumentException("ID cannot be less than 1");
        }
        Order found=repository.findById(id);
        if(found==null){
            throw new IllegalStateException("Order not found");
        }
        return found;
    }
    public List<Order> findAll(){
        return repository.findAll();
    }
    public void markOrderPreparing(int id){
        Order order=findOrderById(id);
        order.markPreparing();
        repository.updateOrderStatus(order);
    }
    public void markOrderReady(int id){
        Order order=findOrderById(id);
        order.markReady();
        repository.updateOrderStatus(order);
    }
    public void markOrderPickedUp(int id){
        Order order=findOrderById(id);
        order.markPickedUp();
        repository.updateOrderStatus(order);
    }
    public void markOrderDispatched(int id){
        Order order=findOrderById(id);
        order.markDispatched();
        repository.updateOrderStatus(order);
    }
    public void markOrderOnTheWay(int id){
        Order order=findOrderById(id);
        order.markOnTheWay();
        repository.updateOrderStatus(order);
    }
    public void completeOrder(int id){
        Order order=findOrderById(id);
        order.markCompleted();
        repository.updateOrderStatus(order);
    }
    public void cancelOrder(int id){
        if(id<1){
            throw new IllegalArgumentException("ID cannot be less than 1");
        }
        Order temp=repository.findById(id);
        if(temp==null){
            throw new IllegalArgumentException("Order not found");
        }
        if(temp.getStatus()==OrderStatus.COMPLETED){
                temp.markRefunded();
                repository.updateOrderStatus(temp);
        }else{
            temp.markVoided();
            repository.updateOrderStatus(temp);
        }
    }
}
