package com.example.medical_online_store.service;

import com.example.medical_online_store.dto.OrderItemRequestDTO;
import com.example.medical_online_store.dto.OrderItemResponseDTO;
import com.example.medical_online_store.exception.OrderNotFoundException;
import com.example.medical_online_store.model.Order;
import com.example.medical_online_store.model.OrderItem;
import com.example.medical_online_store.repository.OrderItemRepository;
import com.example.medical_online_store.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Mapping OrderItem entity -> OrderItemResponseDTO
    private OrderItemResponseDTO tResponseDTO(OrderItem item) {
        return new OrderItemResponseDTO(
            item.getId(),
            item.getOrder().getId(),
            item.getMedicineId(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getTotalPrice()
        );
    }

    // Adding item to an existing order
    public OrderItemResponseDTO addItemToOrder(Long orderId, OrderItemRequestDTO requestDTO)
    throws OrderNotFoundException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with ID:" + orderId));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setMedicineId(requestDTO.getMedicineId());
        item.setQuantity(requestDTO.getQuantity());
        item.setUnitPrice(requestDTO.getUnitPrice());

        OrderItem saved = orderItemRepository.save(item);
        recalculateOrderTotal(order);

        return tResponseDTO(saved);
    }

    // Get all items for an order
    public List<OrderItemResponseDTO> getItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
            .stream()
            .map(this::tResponseDTO)
            .collect(Collectors.toList());
    }

    // Get items by medicineId
    public List<OrderItemResponseDTO> getItemsByMedicineId(Long medicineId) {
        return orderItemRepository.findByMedicineId(medicineId)
            .stream()
            .map(this::tResponseDTO)
            .collect(Collectors.toList());
    }

    // Update item quantity
    public OrderItemResponseDTO updateQuantity(Long itemId, int newQuantity) {
        OrderItem item = orderItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("OrderItem not found with ID: " + itemId));

        item.setQuantity(newQuantity);
        OrderItem updated = orderItemRepository.save(item);
        recalculateOrderTotal(item.getOrder());

        return tResponseDTO(updated);
    }

    // Remove item from order
    public void removeItem(Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("OrderItem not found with ID: " + itemId));

        Order order = item.getOrder();
        orderItemRepository.deleteById(itemId);
        recalculateOrderTotal(order);
    }

    // Recalculate and save the order's total amount
    private void recalculateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        double total = items.stream()
            .mapToDouble(OrderItem::getTotalPrice)
            .sum();
        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}