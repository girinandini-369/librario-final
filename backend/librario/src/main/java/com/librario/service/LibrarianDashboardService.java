package com.librario.service;  // make sure this package matches your folder structure

import com.librario.repository.BookRepository;
import com.librario.repository.BorrowRecordRepository;
import com.librario.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LibrarianDashboardService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public Map<String, Long> getDashboardMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("totalBooks", bookRepository.count());
        metrics.put("totalUsers", userRepository.count());
        metrics.put("booksBorrowed", (long) borrowRecordRepository.findByStatusAndReturnDateIsNull("BORROWED").size());
        metrics.put("pendingRequests", (long) borrowRecordRepository.findByStatus("PENDING").size());
        return metrics;
    }
}
