package com.example.medical_online_store.service;

import com.example.medical_online_store.model.Order;
import com.example.medical_online_store.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.medical_online_store.model.OrderStatus;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Creating the Order
    public Order createOrder(Order order) {
        if (order.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }
        if (order.getTotalAmount() == null) {
            throw new RuntimeException("Total amount cannot be null");
        }
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    // Getting all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get order by ID
    public List<Order> getOrderByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Update order status
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepository.save(order);

    }

    // Delete order
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

}
