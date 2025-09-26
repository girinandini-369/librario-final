package com.librario.service;

import com.librario.entity.Transaction;
import com.librario.entity.Payment;
import com.librario.entity.PaymentStatus;
import com.librario.repository.TransactionRepository;
import com.librario.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledNotificationService {

    private final TransactionRepository transactionRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;

    // 1) Due reminder (2 days before due) - runs daily at 07:00
    @Scheduled(cron = "0 0 7 * * *")
    public void sendDueReminders() {
        LocalDate target = LocalDate.now().plusDays(2);
        List<Transaction> list = transactionRepository.findAll();
        for (Transaction t : list) {
            if (t.getDueDate() != null && t.getDueDate().isEqual(target) && ("ISSUED".equalsIgnoreCase(t.getStatus()) || "RENEWED".equalsIgnoreCase(t.getStatus()))) {
                String to = t.getMember().getEmail();
                String subj = "Reminder: Book due in 2 days: " + t.getBook().getTitle();
                String body = "Hi " + t.getMember().getName() + ",\n\nYour borrowed book \"" + t.getBook().getTitle() + "\" is due on " + t.getDueDate() + ". Please return or renew to avoid fines.";
                if (to != null && !to.isBlank()) emailService.sendEmail(to, subj, body);
                notificationService.createNotification(to, "Reminder: \"" + t.getBook().getTitle() + "\" due on " + t.getDueDate());
            }
        }
    }

    // 2) Overdue detection + notify (runs daily at 08:00)
    @Scheduled(cron = "0 0 8 * * *")
    public void detectAndNotifyOverdue() {
        LocalDate today = LocalDate.now();
        List<Transaction> list = transactionRepository.findAll();
        for (Transaction t : list) {
            // only consider active issued/renewed that aren't returned
            if (( "ISSUED".equalsIgnoreCase(t.getStatus()) || "RENEWED".equalsIgnoreCase(t.getStatus()) )
                    && t.getDueDate() != null && t.getReturnDate() == null && today.isAfter(t.getDueDate())) {

                long overdueDays = ChronoUnit.DAYS.between(t.getDueDate(), today);
                double finePerDay = 10.0;
                if (t.getMember() != null && t.getMember().getMembershipPlan() != null &&
                        t.getMember().getMembershipPlan().getFinePerDay() != null) {
                    finePerDay = t.getMember().getMembershipPlan().getFinePerDay();
                }
                double computedFine = overdueDays * finePerDay;

                // update tx: status -> OVERDUE and apply fine (do not overwrite if there's already larger fine)
                boolean changed = false;
                if (!"OVERDUE".equalsIgnoreCase(t.getStatus())) {
                    t.setStatus("OVERDUE");
                    changed = true;
                }
                Double currentFine = t.getFine() == null ? 0.0 : t.getFine();
                if (computedFine > currentFine) {
                    t.setFine(computedFine);
                    changed = true;
                }

                if (changed) {
                    transactionRepository.save(t);

                    String to = t.getMember().getEmail();
                    String subj = "Overdue Alert: " + t.getBook().getTitle();
                    String body = "Hi " + t.getMember().getName() + ",\n\nYour borrowed book \"" + t.getBook().getTitle() + "\" is overdue by " + overdueDays + " days. Current fine: ₹" + t.getFine() + ". Please return or pay the fine.";
                    if (to != null && !to.isBlank()) emailService.sendEmail(to, subj, body);
                    notificationService.createNotification(to, "Overdue: \"" + t.getBook().getTitle() + "\" — fine: ₹" + t.getFine());

                    // Optionally, create a Payment record in DB so UI can pick it up (if you want)
                    // Not creating Payment here, as your PaymentService handles order creation.
                }
            }
        }
    }
}
