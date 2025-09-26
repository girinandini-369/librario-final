package com.librario.repository;

import com.librario.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // 📊 Returns how many copies are currently borrowed across all books
    @Query("SELECT COALESCE(SUM(b.totalCopies - b.availableCopies), 0) FROM Book b")
    Long countBorrowedBooks();

    // 🔎 General search across title, author, genre
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(
            String title, String author, String genre);

    // 🔎 Strict field searches
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByGenreIgnoreCase(String genre);

    List<Book> findByPublisherContainingIgnoreCase(String publisher);

    List<Book> findByIsbnContainingIgnoreCase(String isbn);
}
