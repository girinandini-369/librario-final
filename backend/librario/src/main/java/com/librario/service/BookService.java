package com.librario.service;

import com.librario.entity.Book;
import com.librario.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // ✅ Add a new book
    public Book addBook(Book book) {
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        } else if (book.getAvailableCopies() > book.getTotalCopies()) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        updateBookStatus(book);
        return bookRepository.save(book);
    }

    // ✅ Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // ✅ Get book by ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // ✅ Update book
    public Book updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setGenre(updatedBook.getGenre());
                    book.setPublisher(updatedBook.getPublisher());
                    book.setYear(updatedBook.getYear());
                    book.setIsbn(updatedBook.getIsbn());
                    book.setBookshelf(updatedBook.getBookshelf());

                    if (updatedBook.getTotalCopies() != null) {
                        book.setTotalCopies(updatedBook.getTotalCopies());
                        if (book.getAvailableCopies() == null ||
                                book.getAvailableCopies() > updatedBook.getTotalCopies()) {
                            book.setAvailableCopies(updatedBook.getTotalCopies());
                        }
                    }

                    if (updatedBook.getAvailableCopies() != null) {
                        int available = Math.min(
                                updatedBook.getAvailableCopies(),
                                book.getTotalCopies() != null ? book.getTotalCopies() : updatedBook.getAvailableCopies()
                        );
                        book.setAvailableCopies(available);
                    }

                    book.setStatus(updatedBook.getStatus());
                    updateBookStatus(book);
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
    }

    // ✅ Delete book
    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ✅ General search (title OR author OR genre)
    public List<Book> searchBooks(String query) {
        return bookRepository
                .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrGenreContainingIgnoreCase(query, query, query);
    }

    // ✅ Strict search helpers
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    public List<Book> searchByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }

    public List<Book> searchByPublisher(String publisher) {
        return bookRepository.findByPublisherContainingIgnoreCase(publisher);
    }

    public List<Book> searchByIsbn(String isbn) {
        return bookRepository.findByIsbnContainingIgnoreCase(isbn);
    }

    // ✅ Advanced search (priority order)
    public List<Book> advancedSearch(String title, String author, String genre, String publisher, String isbn) {
        if (title != null && !title.isBlank()) {
            return searchByTitle(title);
        } else if (author != null && !author.isBlank()) {
            return searchByAuthor(author);
        } else if (genre != null && !genre.isBlank()) {
            return searchByGenre(genre);
        } else if (publisher != null && !publisher.isBlank()) {
            return searchByPublisher(publisher);
        } else if (isbn != null && !isbn.isBlank()) {
            return searchByIsbn(isbn);
        }
        return bookRepository.findAll();
    }

    // ✅ Dashboard stats
    public long getTotalBooks() {
        return bookRepository.count();
    }

    public long getBorrowedBooks() {
        return bookRepository.countBorrowedBooks();
    }

    public long getAvailableBooks() {
        return bookRepository.findAll().stream()
                .mapToLong(b -> b.getAvailableCopies() != null ? b.getAvailableCopies() : 0)
                .sum();
    }

    // ✅ Auto-update availability status
    private void updateBookStatus(Book book) {
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            book.setStatus("OUT_OF_STOCK");
        } else {
            book.setStatus("AVAILABLE");
        }
    }
}
