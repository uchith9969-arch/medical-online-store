package com.example.medical_online_store.controller;

import com.example.medical_online_store.dto.PaymentRequestDTO;
import com.example.medical_online_store.dto.PaymentResponseDTO;
import com.example.medical_online_store.exception.PaymentNotFoundException;
import com.example.medical_online_store.model.PaymentStatus;
import com.example.medical_online_store.model.PaymentType;
import com.example.medical_online_store.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // CREATE - Process immediate payment
    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        try {
            PaymentResponseDTO response = paymentService.processPayment(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // CREATE - Create pending payment
    @PostMapping("/pending")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PaymentResponseDTO> createPendingPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        PaymentResponseDTO response = paymentService.createPendingPayment(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ - Get all payments
    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // READ - Get payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        try {
            PaymentResponseDTO payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (PaymentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // READ - Get payment by order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        try {
            PaymentResponseDTO payment = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(payment);
        } catch (PaymentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // READ - Get payments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByStatus(@PathVariable String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            List<PaymentResponseDTO> payments = paymentService.getPaymentsByStatus(paymentStatus);
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // READ - Get payments by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByType(@PathVariable String type) {
        try {
            PaymentType paymentType = PaymentType.valueOf(type.toUpperCase());
            List<PaymentResponseDTO> payments = paymentService.getPaymentsByType(paymentType);
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // READ - Search by patient name
    @GetMapping("/search")
    public ResponseEntity<List<PaymentResponseDTO>> searchByPatientName(@RequestParam String patientName) {
        List<PaymentResponseDTO> payments = paymentService.searchByPatientName(patientName);
        return ResponseEntity.ok(payments);
    }

    // UPDATE - Update payment status
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            PaymentStatus status = PaymentStatus.valueOf(statusStr.toUpperCase());
            PaymentResponseDTO updated = paymentService.updatePaymentStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (PaymentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE - Complete payment (UNPAID -> PAID)
    @PutMapping("/{id}/complete")
    public ResponseEntity<PaymentResponseDTO> completePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDTO requestDTO) {
        try {
            PaymentResponseDTO updated = paymentService.completePayment(id, requestDTO);
            return ResponseEntity.ok(updated);
        } catch (PaymentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> updatePayment(
            @PathVariable Long id,
            @RequestBody PaymentRequestDTO requestDTO) {

        return ResponseEntity.ok(
                paymentService.updatePayment(id, requestDTO)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Payment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (PaymentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}