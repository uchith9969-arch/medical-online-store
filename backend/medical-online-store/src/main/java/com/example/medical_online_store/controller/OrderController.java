package com.example.medical_online_store.controller;

import com.example.medical_online_store.model.Order;
import com.example.medical_online_store.model.OrderStatus;
import com.example.medical_online_store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin

public class OrderController {
    @Autowired
    private OrderService orderService;

    // Creating the order
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    // Get all orders
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Get Order by ID
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // Get order by User ID
    @GetMapping("/user/{userId}")
    public List<Order> getOrderByUser(@PathVariable Long userId) {
        return orderService.getOrderByUser(userId);
    }

    // Get order by status
    @GetMapping("/status/{status}")
    public List<Order> getOrderByStatus(@PathVariable String status) {

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
        return orderService.getOrdersByStatus(orderStatus);
    }

    // Get Orders between dates
    @GetMapping("/date-range")
    public List<Order> getOrdersBetweenDates(

            @RequestParam String start,
            @RequestParam String end) {

        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();

        LocalDateTime endDate = LocalDate.parse(end).atTime(23, 59, 59);

        return orderService.getOrdersBetweenDates(startDate, endDate);

    }

    // Update order status
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {

        if (body == null || !body.containsKey("status")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        String statusStr = body.get("status");

        OrderStatus status;
        try {

            status = OrderStatus.valueOf(statusStr.toUpperCase());

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + statusStr);

        }
        return orderService.updateOrderStatus(id, status);
    }

    // Cancelling an order
    @PutMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    // Delete order
    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "Order deleted successfully";
    }
}
