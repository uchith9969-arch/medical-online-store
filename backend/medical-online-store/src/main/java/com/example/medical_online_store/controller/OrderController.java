package com.example.medical_online_store.controller;

import com.example.medical_online_store.model.OrderStatus;
import com.example.medical_online_store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.medical_online_store.dto.OrderResponseDTO;
import com.example.medical_online_store.dto.OrderRequestDTO;
import jakarta.validation.Valid;

import java.util.HashMap;
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
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderRequestDTO requestDTO){

        return orderService.createOrder(requestDTO);
        
    }

    // Get all orders
    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Get Order by ID
    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    // Get order by User ID
    @GetMapping("/user/{userId}")
    public List<OrderResponseDTO> getOrderByUser(@PathVariable Long userId) {
        return orderService.getOrderByUser(userId);
    }

    // Get order by status
    @GetMapping("/status/{status}")
    public List<OrderResponseDTO> getOrderByStatus(@PathVariable String status) {

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status: " + status);
        }
        return orderService.getOrdersByStatus(orderStatus);
    }

    // Get Orders between dates
    @GetMapping("/date-range")
    public List<OrderResponseDTO> getOrdersBetweenDates(

            @RequestParam String start,
            @RequestParam String end) {

        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();

        LocalDateTime endDate = LocalDate.parse(end).atTime(23, 59, 59);

        return orderService.getOrdersBetweenDates(startDate, endDate);

    }

    // Update order status
    @PutMapping("/{id}/status")
    public OrderResponseDTO updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {

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
    public OrderResponseDTO cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    // Delete order
    @DeleteMapping("/{id}")
    public Map<String, String> deleteOrder(@PathVariable Long id){

        orderService.deleteOrder(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted successfully");

        return response;
    }
}
