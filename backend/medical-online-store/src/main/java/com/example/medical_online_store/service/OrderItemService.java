package com.example.medical_online_store.service;

import com.example.medical_online_store.dto.OrderItemRequestDTO;
import com.example.medical_online_store.dto.OrderItemResponseDTO;
import com.example.medical_online_store.exception.OrderNotFoundException;
import com.example.medical_online_store.model.Order;
import com.example.medical_online_store.model.OrderItem;
import com.example.medical_online_store.model.OrderStatus;
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

    // Find order or throw checked exception
    private Order findOrderById(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    // Find order item or throw runtime exception
    private OrderItem findOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + id));
    }

    // Map OrderItem entity -> OrderItemResponseDTO
    private OrderItemResponseDTO toResponseDTO(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getOrder().getId(),
                item.getMedicineId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice());
    }

    // Add item to an existing order
    public OrderItemResponseDTO addItemToOrder(Long orderId, OrderItemRequestDTO requestDTO)
            throws OrderNotFoundException {
        Order order = findOrderById(orderId);

        // Prevent adding items to a cancelled or delivered order
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot add items to a " + order.getStatus() + " order");
        }

        // Fetch medicine and get price automatically
        Medicine medicine = medicineService.findMedicineById(requestDTO.getMedicineId());

        // Check medicine is in stock
        if (medicine.getStockQuantity() < requestDTO.getQuantity()) {

            throw new RuntimeException("Insufficient stock for medicine: " + medicine.getName() + ". Available: "
                    + medicine.getStockQuantity + ", Requested: " + requestDTO.getQuantity());
        }

        OrderItem item = new OrderItem(
                order,
                medicine.getId(),
                requestDTO.getQuantity(),
                medicine.getPrice() // price fetched from Medicine, not from client
        );

        OrderItem saved = orderItemRepository.save(item);
        recalculateOrderTotal(order);

        return toResponseDTO(saved);
    }

    // Get all items for an order
    public List<OrderItemResponseDTO> getItemsByOrder(Long orderId) throws OrderNotFoundException {
        findOrderById(orderId); // validates order exists
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get items by medicine ID
    public List<OrderItemResponseDTO> getItemsByMedicine(Long medicineId) {
        return orderItemRepository.findByMedicineId(medicineId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Update quantity of an order item
    public OrderItemResponseDTO updateItemQuantity(Long itemId, Integer newQuantity) throws MedicineNotFoundException {
        if (newQuantity == null || newQuantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        OrderItem item = findOrderItemById(itemId);
        Order order = item.getOrder();

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot update items in a " + order.getStatus() + " order");
        }

        // Check medicine stock for new quantity
        Medicine medicine = medicineService.findMedicineById(item.getMedicineId());
        if (medicine.getStockQuantity() < newQuantity) {
            throw new RuntimeException("Insufficient stock for medicine: " + medicine.getName() + ". Available: "
                    + medicine.getStockQuantity() + ", Requested: " + newQuantity);
        }

        item.setQuantity(newQuantity);
        OrderItem updated = orderItemRepository.save(item);
        recalculateOrderTotal(order);

        return toResponseDTO(updated);
    }

    // Remove an item from an order
    public void removeItemFromOrder(Long itemId) {
        OrderItem item = findOrderItemById(itemId);
        Order order = item.getOrder();

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot remove items from a " + order.getStatus() + " order");
        }

        orderItemRepository.delete(item);
        recalculateOrderTotal(order);
    }

    // Recalculate and update the total amount of an order
    private void recalculateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        Double total = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}