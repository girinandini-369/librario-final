package com.librario.service;

import com.librario.dto.CreatePaymentResponse;
import com.librario.dto.DummyPayRequest;
import com.librario.dto.PaymentDTO;
import com.librario.dto.VerifyPaymentRequest;
import com.librario.entity.*;
import com.librario.repository.MemberRepository;
import com.librario.repository.PaymentRepository;
import com.librario.repository.TransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    @Value("${razorpay.key_id}")
    private String razorKeyId;

    @Value("${razorpay.key_secret}")
    private String razorKeySecret;

    // ===================== Create order for fine =====================
    public CreatePaymentResponse createOrderForTransaction(Long transactionId) throws Exception {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        if (tx.getFine() == null || tx.getFine() <= 0.0) {
            throw new RuntimeException("No fine to pay for this transaction");
        }

        long amountPaise = Math.round(tx.getFine() * 100.0);

        RazorpayClient client = new RazorpayClient(razorKeyId, razorKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + transactionId);
        orderRequest.put("payment_capture", 1);

        Order order = client.Orders.create(orderRequest);
        String orderId = order.get("id").toString();

        Payment payment = Payment.builder()
                .transaction(tx)
                .orderId(orderId)
                .amount(amountPaise)
                .currency("INR")
                .status(PaymentStatus.CREATED)
                .build();
        paymentRepository.save(payment);

        return CreatePaymentResponse.builder()
                .orderId(orderId)
                .keyId(razorKeyId)
                .amount(amountPaise)
                .currency("INR")
                .transactionId(transactionId)
                .build();
    }

    // ===================== Verify Razorpay payment =====================
    public boolean verifyAndMarkPaid(VerifyPaymentRequest req) throws Exception {
        String payload = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();
        String expected = hmacSha256(payload, razorKeySecret);

        if (!expected.equals(req.getRazorpaySignature())) {
            return false;
        }

        Optional<Payment> opt = paymentRepository.findByOrderId(req.getRazorpayOrderId());
        Payment payment = opt.orElseThrow(() -> new RuntimeException("Payment record not found for order"));

        payment.setPaymentId(req.getRazorpayPaymentId());
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        if (payment.getTransaction() != null) {
            Transaction tx = payment.getTransaction();
            tx.setFine(0.0);
            transactionRepository.save(tx);
        } else {
            // It's possible this payment is for membership (if you store such mapping).
            // If you plan to support verifying membership payments via this route, you'll
            // need to locate the member by receipt or an alternative mapping and apply upgrade here.
        }

        return true;
    }

    private String hmacSha256(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    // ===================== Dummy payment for fine =====================
    public Payment dummyPay(DummyPayRequest req) {
        if (req.getTransactionId() == null) {
            throw new RuntimeException("transactionId is required for dummy payment");
        }

        Transaction tx = transactionRepository.findById(req.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + req.getTransactionId()));

        if (tx.getFine() == null || tx.getFine() <= 0.0) {
            throw new RuntimeException("No fine to pay for this transaction");
        }

        long amountPaise = Math.round(tx.getFine() * 100.0);

        Payment payment = Payment.builder()
                .transaction(tx)
                .orderId("DUMMY_ORDER_" + req.getTransactionId())
                .paymentId("DUMMY_PAYMENT_" + System.currentTimeMillis())
                .amount(amountPaise)
                .currency("INR")
                .status(PaymentStatus.PAID)
                .build();

        // Mark fine cleared
        tx.setFine(0.0);
        transactionRepository.save(tx);

        Payment saved = paymentRepository.save(payment);

        // Optional email notify: send confirmation to admin and (member) via EmailService
        if (tx.getMember() != null && tx.getMember().getEmail() != null && !tx.getMember().getEmail().isBlank()) {
            String subj = "Fine Payment Received (Transaction #" + tx.getId() + ")";
            String body = "Hi " + tx.getMember().getName() + ",\n\nWe have received your payment for the fine of ₹" + (amountPaise / 100.0)
                    + " for the book \"" + tx.getBook().getTitle() + "\". Thank you.";
            emailService.sendEmail(tx.getMember().getEmail(), subj, body);
            emailService.sendEmailToAdmin("Fine Paid: " + tx.getBook().getTitle(), "Member " + tx.getMember().getName() + " paid fine for transaction " + tx.getId());
        }

        return saved;
    }

    // ===================== Dummy membership payment (NEW) =====================
    /**
     * Simulate membership payment (no Razorpay). Upgrades the member to PREMIUM,
     * creates a Payment record, and sends emails.
     *
     * @param memberId id of the member to upgrade
     * @return created Payment (status=PAID)
     */
    @Transactional
    public Payment dummyMembershipPay(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));

        // Define membership charge (₹500)
        long amountPaise = 500 * 100L;

        // Create payment record
        Payment payment = Payment.builder()
                .orderId("DUMMY_MEM_" + memberId)
                .paymentId("DUMMY_PAYMENT_MEM_" + System.currentTimeMillis())
                .amount(amountPaise)
                .currency("INR")
                .status(PaymentStatus.PAID)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Upgrade member
        member.setMembershipType("PREMIUM");
        member.setStartDate(LocalDate.now());
        member.setEndDate(LocalDate.now().plusYears(1)); // change as you prefer
        memberRepository.save(member);

        // Send emails
        String subjUser = "Membership Upgraded to PREMIUM";
        String bodyUser = "Hi " + member.getName() + ",\n\nYour membership has been upgraded to PREMIUM. Thank you for upgrading!";

        emailService.sendEmail(member.getEmail(), subjUser, bodyUser);
        emailService.sendEmailToAdmin("Member Upgraded: " + member.getName(), "Member " + member.getName() + " (id: " + memberId + ") upgraded to PREMIUM.");

        return savedPayment;
    }

    // ===================== Admin view =====================
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPaymentsDTO() {
        return paymentRepository.findAllWithTransactionAndMember()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ===================== Member view =====================
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByMemberDTO(Long memberId) {
        return paymentRepository.findByTransaction_Member_IdFetch(memberId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ===================== DTO Mapper =====================
    private PaymentDTO mapToDTO(Payment payment) {
        Transaction tx = payment.getTransaction();
        Member member = (tx != null ? tx.getMember() : null);

        return PaymentDTO.builder()
                .id(payment.getId())
                .transactionId(tx != null ? tx.getId() : null)
                .memberId(member != null ? member.getId() : null)
                .memberName(member != null ? member.getName() : "Unknown")
                .amount(payment.getAmount() / 100.0)
                .currency(payment.getCurrency())
                .status(payment.getStatus() != null ? payment.getStatus().name() : "UNKNOWN")
                .orderId(payment.getOrderId())
                .paymentId(payment.getPaymentId())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    // ===================== Membership upgrade (Razorpay real order) =====================
    public CreatePaymentResponse createMembershipOrder(Long memberId) throws Exception {
        long amountPaise = 500 * 100L;

        RazorpayClient client = new RazorpayClient(razorKeyId, razorKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "membership_" + memberId);
        orderRequest.put("payment_capture", 1);

        Order order = client.Orders.create(orderRequest);
        String orderId = order.get("id").toString();

        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amountPaise)
                .currency("INR")
                .status(PaymentStatus.CREATED)
                .build();
        paymentRepository.save(payment);

        return CreatePaymentResponse.builder()
                .orderId(orderId)
                .keyId(razorKeyId)
                .amount(amountPaise)
                .currency("INR")
                .transactionId(null)
                .build();
    }
}
