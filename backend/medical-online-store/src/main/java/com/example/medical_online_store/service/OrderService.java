package com.example.medical_online_store.service;

import com.example.medical_online_store.dto.OrderItemRequestDTO;
import com.example.medical_online_store.dto.OrderRequestDTO;
import com.example.medical_online_store.dto.OrderResponseDTO;
import com.example.medical_online_store.exception.MedicineNotFoundException;
import com.example.medical_online_store.exception.OrderNotFoundException;
import com.example.medical_online_store.model.*;
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

    @Autowired
    private MedicineService medicineService;

    // Helper: find order or throw checked exception
    private Order findOrderById(Long id) throws OrderNotFoundException {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + id));
    }

    // Map Order entity -> OrderResponseDTO
    // Uses polymorphic methods: getOrderType(), getEntitySummary(), calculatePriority()
    private OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getOrderDate(),
                order.getOrderType(),           // POLYMORPHISM
                order.getEntitySummary(),        // POLYMORPHISM
                order.calculatePriority()        // POLYMORPHISM
        );
    }

    // Create order — creates RegularOrder or UrgentOrder based on orderType
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO)
            throws OrderNotFoundException, MedicineNotFoundException {

        // Decide which subclass to instantiate
        Order order;
        String orderType = requestDTO.getOrderType();

        if ("URGENT".equalsIgnoreCase(orderType)) {
            order = new UrgentOrder(requestDTO.getUserId(), 500.0);
        } else {
            order = new RegularOrder(requestDTO.getUserId());
        }

        order.setTotalAmount(0.0);
        Order savedOrder = orderRepository.save(order);

        // Save items and calculate total
        if (requestDTO.getItems() != null && !requestDTO.getItems().isEmpty()) {
            for (OrderItemRequestDTO itemDTO : requestDTO.getItems()) {

                Medicine medicine = medicineService.findMedicineById(itemDTO.getMedicineId());

                if (medicine.getStockQuantity() < itemDTO.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for medicine: " + medicine.getName()
                            + ". Available: " + medicine.getStockQuantity()
                            + ", Requested: " + itemDTO.getQuantity());
                }

                OrderItem item = new OrderItem(
                        savedOrder,
                        medicine.getId(),
                        itemDTO.getQuantity(),
                        medicine.getPrice()
                );
                orderItemRepository.save(item);
            }
            recalculateOrderTotal(savedOrder);
        }

        // If urgent, add urgency fee to total
        if (savedOrder instanceof UrgentOrder) {
            UrgentOrder urgentOrder = (UrgentOrder) savedOrder;
            savedOrder.setTotalAmount(savedOrder.getTotalAmount() + urgentOrder.getUrgencyFee());
            orderRepository.save(savedOrder);
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

    // Cancel order
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

    // Delete order
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