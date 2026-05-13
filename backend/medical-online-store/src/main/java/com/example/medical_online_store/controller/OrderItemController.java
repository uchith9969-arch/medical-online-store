package com.example.medical_online_store.controller;

import com.example.medical_online_store.dto.OrderItemRequestDTO;
import com.example.medical_online_store.dto.OrderItemResponseDTO;
import com.example.medical_online_store.exception.MedicineNotFoundException;
import com.example.medical_online_store.exception.OrderNotFoundException;
import com.example.medical_online_store.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") // Restricting the frontend URL in production (e.g. "http://localhost:3000")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // Add item to an order
    @PostMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemResponseDTO addItem(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemRequestDTO requestDTO) throws OrderNotFoundException, MedicineNotFoundException {
        return orderItemService.addItemToOrder(orderId, requestDTO);
    }

    // Get all items for an order
    @GetMapping("/{orderId}/items")
    public List<OrderItemResponseDTO> getItemsByOrder(@PathVariable Long orderId) throws OrderNotFoundException {
        return orderItemService.getItemsByOrder(orderId);
    }

    // Get all order items by medicine
    @GetMapping("/items/medicine/{medicineId}")
    public List<OrderItemResponseDTO> getItemsByMedicine(@PathVariable Long medicineId) {
        return orderItemService.getItemsByMedicine(medicineId);
    }

    // Update quantity of an order item
    @PutMapping("/items/{itemId}/quantity")
    public OrderItemResponseDTO updateQuantity(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) throws MedicineNotFoundException {
        Integer newQuantity = body.get("quantity");
        return orderItemService.updateItemQuantity(itemId, newQuantity);
    }

    // Remove an item from an order
    @DeleteMapping("/items/{itemId}")
    public Map<String, String> removeItem(@PathVariable Long itemId) {
        orderItemService.removeItemFromOrder(itemId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order item removed successfully");
        return response;
    }
}