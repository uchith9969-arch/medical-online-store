package com.example.medical_online_store.service;

import com.example.medical_online_store.model.Order;
import com.example.medical_online_store.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.medical_online_store.model.OrderStatus;
import com.example.medical_online_store.exception.OrderNotFoundException;

import java.time.LocalDateTime;
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
        if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            throw new RuntimeException("Total amount must be greater than 0");
        }
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    // Getting all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Get Order by ID
    public Order getOrderById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    // Get order by User ID
    public List<Order> getOrderByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Get order by status
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // Get orders between dates
    public List<Order> getOrdersBetweenDates(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByOrderDateBetween(start, end);
    }

    // Update order status
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));

        order.setStatus(status);
        return orderRepository.save(order);

    }

    // Cancel the Order
    public Order cancelOrder(Long id) {

        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        // Prevent canceling a delivered Order
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Delivered orders cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    // Delete order
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

}
