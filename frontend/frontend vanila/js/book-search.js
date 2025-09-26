// This script fetches books from the backend API and displays them

document.addEventListener('DOMContentLoaded', () => {
    const booksList = document.getElementById('books-list');
    const searchInput = document.getElementById('search-input');
    let books = [];

    // Fetch books from the backend API
    async function fetchBooks() {
        try {
            const response = await fetch('http://localhost:8080/api/books');
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            books = await response.json();
            renderBooks(books);
        } catch (error) {
            console.error('Error fetching books:', error);
            booksList.innerHTML = "<p>Failed to load books. Please try again later.</p>";
        }
    }

    // Render books on the page
    function renderBooks(bookArray) {
        booksList.innerHTML = "";
        if (bookArray.length === 0) {
            booksList.innerHTML = "<p>No books found.</p>";
            return;
        }
        bookArray.forEach(book => {
            const div = document.createElement('div');
            div.className = 'book-card';
            div.innerHTML = `
                <div class="book-title">${book.title}</div>
                <div class="book-author">Author: ${book.author}</div>
                <div class="book-genre">Genre: ${book.genre}</div>
                <div class="book-status ${book.availableCopies > 0 ? 'available' : 'unavailable'}">
                    ${book.availableCopies > 0 ? 'Available' : 'Unavailable'}
                </div>
                <button class="borrow-btn ${book.availableCopies === 0 ? 'disabled' : ''}" ${book.availableCopies === 0 ? 'disabled' : ''}>
                    ${book.availableCopies > 0 ? 'Borrow' : 'Not Available'}
                </button>
            `;
            booksList.appendChild(div);
        });
    }

    // Filter books based on search input
    searchInput.addEventListener('input', () => {
        const query = searchInput.value.toLowerCase();
        const filteredBooks = books.filter(book =>
            book.title.toLowerCase().includes(query) ||
            book.author.toLowerCase().includes(query)
        );
        renderBooks(filteredBooks);
    });

    // Initial load
    fetchBooks();
});
