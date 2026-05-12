package com.example.medical_online_store.controller;

import com.example.medical_online_store.dto.OrderRequestDTO;
import com.example.medical_online_store.dto.OrderResponseDTO;
import com.example.medical_online_store.exception.OrderNotFoundException;
import com.example.medical_online_store.model.OrderStatus;
import com.example.medical_online_store.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") // Restricting the frontend URL in production (e.g. "http://localhost:3000" )
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Creating an order
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderRequestDTO requestDTO) throws OrderNotFoundException {
        return orderService.createOrder(requestDTO);
    }

    // Get all orders
    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Get an order by ID
    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable Long id) throws OrderNotFoundException {
        return orderService.getOrderById(id);
    }

    // Get orders by user ID
    @GetMapping("/user/{userId}")
    public List<OrderResponseDTO> getOrderByUser(@PathVariable Long userId) {
        return orderService.getOrderByUser(userId);
    }

    // Get orders by status
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

    // Get orders between dates
    @GetMapping("/date-range")
    public List<OrderResponseDTO> getOrdersBetweenDates(
            @RequestParam String start,
            @RequestParam String end) {

        LocalDateTime startDate;
        LocalDateTime endDate;

        try {
            startDate = LocalDate.parse(start).atStartOfDay();
            endDate = LocalDate.parse(end).atTime(23, 59, 59);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Use yyyy-MM-dd");
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        return orderService.getOrdersBetweenDates(startDate, endDate);
    }

    // Update order status
    @PutMapping("/{id}/status")
    public OrderResponseDTO updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) throws OrderNotFoundException {
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

    // Cancelling order
    @PutMapping("/{id}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable Long id) throws OrderNotFoundException {
        return orderService.cancelOrder(id);
    }

    // Deleting order
    @DeleteMapping("/{id}")
    public Map<String, String> deleteOrder(@PathVariable Long id) throws OrderNotFoundException {
        orderService.deleteOrder(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted successfully");
        return response;
    }
}