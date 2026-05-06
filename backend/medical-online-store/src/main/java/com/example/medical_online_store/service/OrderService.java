package com.example.medical_online_store.service;

import com.example.medical_online_store.model.Order;
import com.example.medical_online_store.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Creating the Order
    public Order creatOrder(Order order) {
        return orderRepository.save(order);
    }

    // Getting all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    // Update order status
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    // Delete order
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

}
