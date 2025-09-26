package com.librario.controller;

import com.librario.entity.Book;
import com.librario.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librarian/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500") // frontend local
public class LibrarianBooksController {

    private final BookService bookService;

    // Get all books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // Get a book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id " + id));
        return ResponseEntity.ok(book);
    }

    // Add a new book
    @PostMapping("/add")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookService.addBook(book));
    }

    // Update a book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    // Delete a book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Search books
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(bookService.searchBooks(query));
    }
}
