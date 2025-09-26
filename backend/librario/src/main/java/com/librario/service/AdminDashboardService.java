package com.librario.service;

import com.librario.repository.BookRepository;
import com.librario.repository.BorrowRecordRepository;
import com.librario.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public long getTotalBooks() {
        return bookRepository.count();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getBooksBorrowed() {
        Long borrowed = bookRepository.countBorrowedBooks();
        return borrowed != null ? borrowed : 0;
    }

    public long getActiveBorrowRecords() {
        return borrowRecordRepository.findByStatus("BORROWED").size();
    }
}
