package com.example.medical_online_store.repository;

import com.example.medical_online_store.model.Payment;
import com.example.medical_online_store.model.PaymentStatus;
import com.example.medical_online_store.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find by order ID
    Optional<Payment> findByOrderId(Long orderId);

    // Find by transaction ID
    Optional<Payment> findByTransactionId(String transactionId);

    // Find by payment status
    List<Payment> findByPaymentStatus(PaymentStatus status);

    // Find by payment type
    List<Payment> findByPaymentType(PaymentType type);

    // Find by patient name
    List<Payment> findByPatientNameContainingIgnoreCase(String patientName);

    // Find payments between dates
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);

}