package com.example.medical_online_store.service;

import com.example.medical_online_store.dto.OrderItemRequestDTO;
import com.example.medical_online_store.dto.OrderRequestDTO;
import com.example.medical_online_store.dto.OrderResponseDTO;
import com.example.medical_online_store.exception.OrderNotFoundException;
import com.example.medical_online_store.model.Order;
import com.example.medical_online_store.model.OrderItem;
import com.example.medical_online_store.model.OrderStatus;
import com.example.medical_online_store.repository.OrderItemRepository;
import com.example.medical_online_store.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    //Finding order or throwing checked exception
    private Order findOrderById(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    // Map Order entity -> OrderResponseDTO
    private OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderDate()
        );
    }

    // Creating order (with optional items)
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) throws OrderNotFoundException {
        Order order = new Order();
        order.setUserId(requestDTO.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(0.0);

        Order savedOrder = orderRepository.save(order);

        // If items are provided, save them and calculate total
        if (requestDTO.getItems() != null && !requestDTO.getItems().isEmpty()) {
            for (OrderItemRequestDTO itemDTO : requestDTO.getItems()) {
                OrderItem item = new OrderItem(
                        savedOrder,
                        itemDTO.getMedicineId(),
                        itemDTO.getQuantity(),
                        itemDTO.getUnitPrice()
                );
                orderItemRepository.save(item);
            }
            recalculateOrderTotal(savedOrder);
        }

        return toResponseDTO(findOrderById(savedOrder.getId()));
    }

    // Get all orders
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get order by ID
    public OrderResponseDTO getOrderById(Long id) throws OrderNotFoundException {
        return toResponseDTO(findOrderById(id));
    }

    // Get orders by user ID
    public List<OrderResponseDTO> getOrderByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get orders by status
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Get orders between dates
    public List<OrderResponseDTO> getOrdersBetweenDates(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByOrderDateBetween(start, end)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Update order status
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) throws OrderNotFoundException {
        Order order = findOrderById(id);
        order.setStatus(status);
        return toResponseDTO(orderRepository.save(order));
    }

    // Cancelling order
    public OrderResponseDTO cancelOrder(Long id) throws OrderNotFoundException {
        Order order = findOrderById(id);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Delivered orders cannot be cancelled");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toResponseDTO(orderRepository.save(order));
    }

    // Deleting order
    public void deleteOrder(Long id) throws OrderNotFoundException {
        Order order = findOrderById(id);
        orderRepository.delete(order);
    }

    // Recalculate and update the total amount of an order
    public void recalculateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        Double total = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}