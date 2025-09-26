package com.librario.service;

import com.librario.dto.TransactionResponse;
import com.librario.entity.Book;
import com.librario.entity.Member;
import com.librario.entity.MembershipPlan;
import com.librario.entity.Transaction;
import com.librario.repository.BookRepository;
import com.librario.repository.MemberRepository;
import com.librario.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    private static final int LOW_STOCK_THRESHOLD = 2; // configurable

    // ---------------- ISSUE ----------------
    public TransactionResponse issueBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new RuntimeException("Book is not available");
        }

        MembershipPlan plan = member.getMembershipPlan();
        int borrowingLimit = (plan != null && plan.getBorrowingLimit() != null)
                ? plan.getBorrowingLimit() : Integer.MAX_VALUE;

        int activeBorrowed = (int) transactionRepository.findByMemberId(memberId)
                .stream()
                .filter(t -> "ISSUED".equalsIgnoreCase(t.getStatus()) || "RENEWED".equalsIgnoreCase(t.getStatus()))
                .count();

        if (activeBorrowed >= borrowingLimit) {
            throw new RuntimeException("Borrowing limit reached. Current: " + activeBorrowed + ", limit: " + borrowingLimit);
        }

        int avail = book.getAvailableCopies() == null ? 0 : book.getAvailableCopies();
        book.setAvailableCopies(avail - 1);
        bookRepository.save(book);

        checkAndNotifyStock(book);

        Transaction tx = Transaction.builder()
                .member(member)
                .book(book)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status("ISSUED")
                .fine(0.0)
                .build();

        Transaction saved = transactionRepository.save(tx);

        // Notify user + admin
        String subj = "Book Issued: " + book.getTitle();
        String body = "Book: " + book.getTitle()
                + "\nMember: " + member.getName() + " (" + member.getEmail() + ")"
                + "\nIssue Date: " + saved.getIssueDate()
                + "\nDue Date: " + saved.getDueDate()
                + "\nTransaction ID: " + saved.getId();

        if (member.getEmail() != null && !member.getEmail().isBlank()) {
            emailService.sendEmail(member.getEmail(), subj, body);
        }
        emailService.sendEmailToAdmin(subj, body);

        notificationService.createNotification(member.getEmail(),
                "Your book \"" + book.getTitle() + "\" has been issued. Due on " + saved.getDueDate());

        return mapToResponse(saved);
    }

    // ---------------- RETURN ----------------
    public TransactionResponse returnBook(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        if (!"ISSUED".equalsIgnoreCase(tx.getStatus()) && !"RENEWED".equalsIgnoreCase(tx.getStatus())) {
            throw new RuntimeException("Transaction is not active (cannot return)");
        }

        Book book = tx.getBook();
        int avail = book.getAvailableCopies() == null ? 0 : book.getAvailableCopies();
        book.setAvailableCopies(avail + 1);
        bookRepository.save(book);

        checkAndNotifyStock(book);

        tx.setReturnDate(LocalDate.now());
        tx.setStatus("RETURNED");

        double fineAmount = 0.0;
        if (tx.getDueDate() != null && tx.getReturnDate() != null && tx.getReturnDate().isAfter(tx.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(tx.getDueDate(), tx.getReturnDate());
            Double finePerDay = (tx.getMember() != null && tx.getMember().getMembershipPlan() != null &&
                    tx.getMember().getMembershipPlan().getFinePerDay() != null)
                    ? tx.getMember().getMembershipPlan().getFinePerDay() : 10.0;
            fineAmount = overdueDays * finePerDay;
        }
        tx.setFine(fineAmount);

        Transaction saved = transactionRepository.save(tx);

        String subj = "Book Returned: " + book.getTitle();
        String body = "Book: " + book.getTitle()
                + "\nMember: " + tx.getMember().getName() + " (" + tx.getMember().getEmail() + ")"
                + "\nReturn Date: " + saved.getReturnDate()
                + "\nFine: " + saved.getFine()
                + "\nTransaction ID: " + saved.getId();

        if (tx.getMember().getEmail() != null && !tx.getMember().getEmail().isBlank()) {
            emailService.sendEmail(tx.getMember().getEmail(), subj, body);
        }
        emailService.sendEmailToAdmin(subj, body);

        notificationService.createNotification(tx.getMember().getEmail(),
                "You returned \"" + book.getTitle() + "\". Fine: ₹" + saved.getFine());

        return mapToResponse(saved);
    }

    // ---------------- MARK RETURNED ----------------
    public TransactionResponse markReturned(Long transactionId) {
        return returnBook(transactionId);
    }

    // ---------------- RENEW ----------------
    public TransactionResponse renewBook(Long transactionId, int extraDays) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        if (!"ISSUED".equalsIgnoreCase(tx.getStatus()) && !"RENEWED".equalsIgnoreCase(tx.getStatus())) {
            throw new RuntimeException("Transaction cannot be renewed");
        }

        LocalDate newDue = (tx.getDueDate() == null ? LocalDate.now() : tx.getDueDate().plusDays(extraDays));
        tx.setDueDate(newDue);
        tx.setStatus("RENEWED");

        Transaction saved = transactionRepository.save(tx);

        String subj = "Book Renewed: " + tx.getBook().getTitle();
        String body = "Book: " + tx.getBook().getTitle()
                + "\nMember: " + tx.getMember().getName() + " (" + tx.getMember().getEmail() + ")"
                + "\nNew Due Date: " + saved.getDueDate()
                + "\nTransaction ID: " + saved.getId();

        if (tx.getMember().getEmail() != null && !tx.getMember().getEmail().isBlank()) {
            emailService.sendEmail(tx.getMember().getEmail(), subj, body);
        }
        emailService.sendEmailToAdmin(subj, body);

        notificationService.createNotification(tx.getMember().getEmail(),
                "Your book \"" + tx.getBook().getTitle() + "\" has been renewed. New due: " + saved.getDueDate());

        return mapToResponse(saved);
    }

    // ---------------- MARK FINE PAID ----------------
    public TransactionResponse markFinePaid(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + transactionId));

        tx.setFine(0.0);
        Transaction saved = transactionRepository.save(tx);

        if (tx.getMember() != null && tx.getMember().getEmail() != null && !tx.getMember().getEmail().isBlank()) {
            String subj = "Fine Paid: Transaction #" + saved.getId();
            String body = "Your fine for \"" + saved.getBook().getTitle() + "\" has been marked paid.";
            emailService.sendEmail(tx.getMember().getEmail(), subj, body);
            notificationService.createNotification(tx.getMember().getEmail(),
                    "Fine for \"" + saved.getBook().getTitle() + "\" has been paid.");
        }

        return mapToResponse(saved);
    }

    // ---------------- LOW STOCK CHECK ----------------
    private void checkAndNotifyStock(Book book) {
        if (book.getAvailableCopies() == null) return;

        if (book.getAvailableCopies() <= 0) {
            String subj = "📕 Book Out of Stock: " + book.getTitle();
            String body = "The book \"" + book.getTitle() + "\" is now OUT OF STOCK.\n\n"
                    + "Total Copies: " + book.getTotalCopies()
                    + "\nAvailable Copies: " + book.getAvailableCopies();
            emailService.sendEmailToAdmin(subj, body);
            notificationService.createNotification("admin@librario.com",
                    "Book \"" + book.getTitle() + "\" is now OUT OF STOCK");
        } else if (book.getAvailableCopies() <= LOW_STOCK_THRESHOLD) {
            String subj = "⚠️ Low Stock Warning: " + book.getTitle();
            String body = "The book \"" + book.getTitle() + "\" is running low on stock.\n\n"
                    + "Total Copies: " + book.getTotalCopies()
                    + "\nAvailable Copies: " + book.getAvailableCopies();
            emailService.sendEmailToAdmin(subj, body);
            notificationService.createNotification("admin@librario.com",
                    "Low stock alert: \"" + book.getTitle() + "\" has only "
                            + book.getAvailableCopies() + " copies left");
        }
    }

    // ---------------- GETTERS ----------------
    public List<TransactionResponse> getTransactionsByMember(Long memberId) {
        return transactionRepository.findByMemberId(memberId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByBook(Long bookId) {
        return transactionRepository.findByBookId(bookId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // overdue (global)
    public List<TransactionResponse> getOverdueTransactions() {
        LocalDate today = LocalDate.now();
        return transactionRepository.findAll().stream()
                .filter(t -> isOverdueRecord(t, today))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private boolean isOverdueRecord(Transaction t, LocalDate today) {
        if (t == null) return false;
        if ("OVERDUE".equalsIgnoreCase(t.getStatus())) return true;
        return (("ISSUED".equalsIgnoreCase(t.getStatus()) || "RENEWED".equalsIgnoreCase(t.getStatus()))
                && t.getDueDate() != null && today.isAfter(t.getDueDate()) && t.getReturnDate() == null);
    }

    private TransactionResponse mapToResponse(Transaction tx) {
        if (tx == null) return null;
        Long memberId = tx.getMember() != null ? tx.getMember().getId() : null;
        Long bookId = tx.getBook() != null ? tx.getBook().getId() : null;
        String bookTitle = tx.getBook() != null ? tx.getBook().getTitle() : null;
        String memberName = tx.getMember() != null ? tx.getMember().getName() : null;

        return TransactionResponse.builder()
                .id(tx.getId())
                .memberId(memberId)
                .bookId(bookId)
                .bookTitle(bookTitle)
                .memberName(memberName)
                .issueDate(tx.getIssueDate())
                .dueDate(tx.getDueDate())
                .returnDate(tx.getReturnDate())
                .fine(tx.getFine())
                .status(tx.getStatus())
                .build();
    }

    // overdue by member
    public List<TransactionResponse> getOverdueTransactionsByMember(Long memberId) {
        LocalDate today = LocalDate.now();
        return transactionRepository.findByMemberId(memberId).stream()
                .filter(t -> isOverdueRecord(t, today))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // returned by member
    public List<TransactionResponse> getReturnedTransactionsByMember(Long memberId) {
        return transactionRepository.findByMemberId(memberId).stream()
                .filter(t -> "RETURNED".equalsIgnoreCase(t.getStatus()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔔 Send due date reminders (2 days before due date)
    public void sendDueReminders() {
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(2);

        List<Transaction> dueSoon = transactionRepository.findAll().stream()
                .filter(t -> ("ISSUED".equalsIgnoreCase(t.getStatus()) || "RENEWED".equalsIgnoreCase(t.getStatus()))
                        && t.getDueDate() != null
                        && t.getReturnDate() == null
                        && t.getDueDate().isEqual(reminderDate))
                .toList();

        for (Transaction tx : dueSoon) {
            String subj = "Reminder: Book Due Soon - " + tx.getBook().getTitle();
            String body = "Hello " + tx.getMember().getName()
                    + ",\n\nYour borrowed book \"" + tx.getBook().getTitle()
                    + "\" is due on " + tx.getDueDate()
                    + ". Please return or renew it on time to avoid fines.";

            emailService.sendEmail(tx.getMember().getEmail(), subj, body);
            emailService.sendEmailToAdmin(subj, body);

            notificationService.createNotification(
                    tx.getMember().getEmail(),
                    "Reminder: Your book \"" + tx.getBook().getTitle() + "\" is due on " + tx.getDueDate()
            );
        }
    }

    // 🔔 Send overdue alerts
    public void sendOverdueAlerts() {
        LocalDate today = LocalDate.now();

        List<Transaction> overdue = transactionRepository.findAll().stream()
                .filter(t -> ("ISSUED".equalsIgnoreCase(t.getStatus()) || "RENEWED".equalsIgnoreCase(t.getStatus()))
                        && t.getDueDate() != null
                        && t.getReturnDate() == null
                        && today.isAfter(t.getDueDate()))
                .toList();

        for (Transaction tx : overdue) {
            String subj = "Overdue Alert: " + tx.getBook().getTitle();
            String body = "Hello " + tx.getMember().getName()
                    + ",\n\nYour borrowed book \"" + tx.getBook().getTitle()
                    + "\" was due on " + tx.getDueDate()
                    + ". Please return it immediately to avoid additional fines.";

            emailService.sendEmail(tx.getMember().getEmail(), subj, body);
            emailService.sendEmailToAdmin(subj, body);

            notificationService.createNotification(
                    tx.getMember().getEmail(),
                    "Overdue Alert: \"" + tx.getBook().getTitle() + "\" was due on " + tx.getDueDate()
            );
        }
    }
}
