package com.example.medical_online_store.controller;

import com.example.medical_online_store.dto.OrderItemRequestDTO;
import com.example.medical_online_store.dto.OrderItemResponseDTO;
import com.example.medical_online_store.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // POST
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItemResponseDTO> addItem(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemRequestDTO dto) {
        return ResponseEntity.ok(orderItemService.addItemToOrder(orderId, dto));
    }

    // GET 
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponseDTO>> getItemsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getItemsByOrderId(orderId));
    }

    // GET Medicine
    @GetMapping("/items/medicine/{medicineId}")
    public ResponseEntity<List<OrderItemResponseDTO>> getItemsByMedicine(@PathVariable Long medicineId) {
        return ResponseEntity.ok(orderItemService.getItemsByMedicineId(medicineId));
    }

    // PUT iitems,quantity
    @PutMapping("/items/{itemId}/quantity")
    public ResponseEntity<OrderItemResponseDTO> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(orderItemService.updateQuantity(itemId, quantity));
    }

    // DELETE items
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        orderItemService.removeItem(itemId);
        return ResponseEntity.noContent().build();
    }
}