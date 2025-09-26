package com.librario.controller;

import com.librario.dto.CreatePaymentResponse;
import com.librario.dto.DummyPayRequest;
import com.librario.dto.PaymentDTO;
import com.librario.dto.VerifyPaymentRequest;
import com.librario.entity.Payment;
import com.librario.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PaymentController {

    private final PaymentService paymentService;

    /** Create order for fine payment */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Long> payload) {
        try {
            Long transactionId = payload.get("transactionId");
            if (transactionId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "transactionId is required"));
            }
            CreatePaymentResponse resp = paymentService.createOrderForTransaction(transactionId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error creating order: " + e.getMessage()));
        }
    }

    /** Verify payment after Razorpay success */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload) {
        try {
            String orderId = firstNonNull(payload.get("razorpay_order_id"), payload.get("razorpayOrderId"));
            String paymentId = firstNonNull(payload.get("razorpay_payment_id"), payload.get("razorpayPaymentId"));
            String signature = firstNonNull(payload.get("razorpay_signature"), payload.get("razorpaySignature"));

            if (orderId == null || paymentId == null || signature == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Missing required payment fields"));
            }

            VerifyPaymentRequest req = VerifyPaymentRequest.builder()
                    .razorpayOrderId(orderId)
                    .razorpayPaymentId(paymentId)
                    .razorpaySignature(signature)
                    .build();

            boolean ok = paymentService.verifyAndMarkPaid(req);
            if (ok) {
                return ResponseEntity.ok(Map.of("status", "success"));
            } else {
                return ResponseEntity.status(400).body(Map.of("status", "signature_mismatch"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error verifying payment: " + e.getMessage()));
        }
    }

    /** Dummy pay for fine (existing) */
    @PostMapping("/dummy-pay")
    public ResponseEntity<?> dummyPay(@RequestBody DummyPayRequest req) {
        try {
            Payment p = paymentService.dummyPay(req);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "paymentId", p.getPaymentId(),
                    "orderId", p.getOrderId(),
                    "transactionId", p.getTransaction() != null ? p.getTransaction().getId() : null
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Dummy pay failed: " + e.getMessage()));
        }
    }

    /**
     * Dummy membership payment endpoint (for testing upgrades without Razorpay)
     * Payload: {"memberId": <id>}
     */
    @PostMapping("/dummy-membership-pay")
    public ResponseEntity<?> dummyMembershipPay(@RequestBody Map<String, Long> payload) {
        try {
            Long memberId = payload.get("memberId");
            if (memberId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "memberId is required"));
            }
            Payment p = paymentService.dummyMembershipPay(memberId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "paymentId", p.getPaymentId(),
                    "orderId", p.getOrderId(),
                    "memberId", memberId
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Dummy membership pay failed: " + e.getMessage()));
        }
    }

    /** Get all payments */
    @GetMapping("/all")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPaymentsDTO());
    }

    /** Get payments by member */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(paymentService.getPaymentsByMemberDTO(memberId));
    }

    /** Create membership upgrade order (real Razorpay flow) */
    @PostMapping("/create-membership-order")
    public ResponseEntity<?> createMembershipOrder(@RequestBody Map<String, Long> payload) {
        try {
            Long memberId = payload.get("memberId");
            if (memberId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "memberId is required"));
            }
            CreatePaymentResponse resp = paymentService.createMembershipOrder(memberId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Error creating membership order: " + e.getMessage()));
        }
    }

    private String firstNonNull(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }
}
