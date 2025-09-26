package com.librario.controller;

import com.librario.dto.MessageResponse;
import com.librario.entity.Book;
import com.librario.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class BookController {

    private final BookService bookService;

    // ✅ Add book
    @PostMapping("/add")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            Book saved = bookService.addBook(book);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("❌ Failed to add book: " + e.getMessage()));
        }
    }

    // ✅ Get all books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // ✅ Get book by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        return bookOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new MessageResponse("❌ Book not found with id " + id)));
    }

    // ✅ Update book
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        try {
            Book updated = bookService.updateBook(id, updatedBook);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new MessageResponse("❌ " + e.getMessage()));
        }
    }

    // ✅ Delete book
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteBook(@PathVariable Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.ok(new MessageResponse("✅ Book deleted successfully"));
        } else {
            return ResponseEntity.status(404)
                    .body(new MessageResponse("❌ Book not found with id " + id));
        }
    }

    // ✅ General search
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(bookService.searchBooks(query));
    }

    // ✅ Strict search endpoints
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> searchByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> searchByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.searchByAuthor(author));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> searchByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(bookService.searchByGenre(genre));
    }

    @GetMapping("/publisher/{publisher}")
    public ResponseEntity<List<Book>> searchByPublisher(@PathVariable String publisher) {
        return ResponseEntity.ok(bookService.searchByPublisher(publisher));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<List<Book>> searchByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.searchByIsbn(isbn));
    }

    // ✅ Advanced search (multiple filters)
    @GetMapping("/filter")
    public ResponseEntity<List<Book>> filterBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String isbn
    ) {
        return ResponseEntity.ok(bookService.advancedSearch(title, author, genre, publisher, isbn));
    }

    // ✅ Dashboard stats
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(new StatsResponse(
                bookService.getTotalBooks(),
                bookService.getBorrowedBooks(),
                bookService.getAvailableBooks()
        ));
    }

    // DTO for stats
    record StatsResponse(long totalBooks, long borrowedBooks, long availableBooks) {}
}
