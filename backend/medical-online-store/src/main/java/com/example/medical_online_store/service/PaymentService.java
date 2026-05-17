package com.example.medical_online_store.service;

import com.example.medical_online_store.dto.PaymentRequestDTO;
import com.example.medical_online_store.dto.PaymentResponseDTO;
import com.example.medical_online_store.exception.PaymentNotFoundException;
import com.example.medical_online_store.model.Payment;
import com.example.medical_online_store.model.PaymentStatus;
import com.example.medical_online_store.model.PaymentType;
import com.example.medical_online_store.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Helper: Find payment or throw exception
    private Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));
    }

    // Helper: Convert Entity to DTO
    private PaymentResponseDTO toResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrderId(),
                payment.getPatientName(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getPaymentType(),
                payment.getCardHolderName(),
                payment.getCardLastFour(),
                payment.getTransactionId(),
                payment.getPaymentDate(),
                payment.getPaymentDate()
        );
    }

    // Helper: Mask card number to last 4 digits
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return null;
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }

    // CREATE - Process Payment
    public PaymentResponseDTO processPayment(PaymentRequestDTO requestDTO) {

        // Check if payment already exists for this order
        paymentRepository.findByOrderId(requestDTO.getOrderId())
                .ifPresent(p -> {
                    throw new RuntimeException("Payment already exists for order ID: " + requestDTO.getOrderId());
                });

        // Create payment entity
        Payment payment = new Payment();
        payment.setOrderId(requestDTO.getOrderId());
        payment.setPatientName(requestDTO.getPatientName());
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentType(requestDTO.getPaymentType());

        // Set payment status to PAID
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());

        // Handle card details if payment type is CARD
        if (requestDTO.getPaymentType() == PaymentType.CARD) {
            payment.setCardHolderName(requestDTO.getCardHolderName());
            payment.setCardLastFour(maskCardNumber(requestDTO.getCardNumber()));
        }

        Payment savedPayment = paymentRepository.save(payment);
        return toResponseDTO(savedPayment);
    }

    // CREATE - Create pending payment
    public PaymentResponseDTO createPendingPayment(PaymentRequestDTO requestDTO) {
        Payment payment = new Payment();
        payment.setOrderId(requestDTO.getOrderId());
        payment.setPatientName(requestDTO.getPatientName());
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentStatus(PaymentStatus.UNPAID);

        Payment savedPayment = paymentRepository.save(payment);
        return toResponseDTO(savedPayment);
    }

    public PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO requestDTO) {

        Payment payment = findPaymentById(id);

        // update basic fields
        payment.setPatientName(requestDTO.getPatientName());
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentType(requestDTO.getPaymentType());

        // update status
        payment.setPaymentStatus(PaymentStatus.PAID);

        // set payment date
        payment.setPaymentDate(LocalDateTime.now());

        // update card details if CARD
        if (requestDTO.getPaymentType() == PaymentType.CARD) {

            payment.setCardHolderName(
                    requestDTO.getCardHolderName()
            );

            if (requestDTO.getCardNumber() != null) {

                payment.setCardLastFour(
                        maskCardNumber(requestDTO.getCardNumber())
                );
            }
        }

        Payment updated = paymentRepository.save(payment);

        return toResponseDTO(updated);
    }

    // READ - Get all payments
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // READ - Get payment by ID
    public PaymentResponseDTO getPaymentById(Long id) {
        return toResponseDTO(findPaymentById(id));
    }

    // READ - Get payment by order ID
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order ID: " + orderId));
        return toResponseDTO(payment);
    }

    // READ - Get payments by status
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // READ - Get payments by type
    public List<PaymentResponseDTO> getPaymentsByType(PaymentType type) {
        return paymentRepository.findByPaymentType(type)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // READ - Search by patient name
    public List<PaymentResponseDTO> searchByPatientName(String patientName) {
        return paymentRepository.findByPatientNameContainingIgnoreCase(patientName)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // UPDATE - Update payment status
    public PaymentResponseDTO updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = findPaymentById(id);
        payment.setPaymentStatus(status);

        // Set payment date when status changes to PAID
        if (status == PaymentStatus.PAID && payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        return toResponseDTO(paymentRepository.save(payment));
    }

    // UPDATE - Complete payment (from UNPAID to PAID)
    public PaymentResponseDTO completePayment(Long id, PaymentRequestDTO requestDTO) {
        Payment payment = findPaymentById(id);

        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Payment is already completed");
        }

        payment.setPaymentType(requestDTO.getPaymentType());
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());

        // Handle card details
        if (requestDTO.getPaymentType() == PaymentType.CARD) {
            payment.setCardHolderName(requestDTO.getCardHolderName());
            payment.setCardLastFour(maskCardNumber(requestDTO.getCardNumber()));
        }

        return toResponseDTO(paymentRepository.save(payment));
    }

    // DELETE
    public void deletePayment(Long id) {
        Payment payment = findPaymentById(id);
        paymentRepository.delete(payment);
    }

}