package com.example.medical_online_store.controller;

import com.example.medical_online_store.model.Order;
import com.example.medical_online_store.model.OrderStatus;
import com.example.medical_online_store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin

public class OrderController {
    @Autowired
    private OrderService orderService;

    // Creating the order
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.creatOrder(order);
    }

    // Get all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Get order by ID
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // Update order status
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            OrderStatus status = OrderStatus.valueOf(body.get("status").toUpperCase());
            return orderService.updateOrderStatus(id, status);
        } catch (Exception e) {
            throw new RuntimeException("Invalid status provided");
        }
    }

    // Delete order
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "Order deleted successfully";
    }
}
