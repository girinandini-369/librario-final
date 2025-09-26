const API_BASE = "http://localhost:8080/api/books";

// Fetch all books
async function fetchBooks() {
    try {
        const res = await fetch(API_BASE);
        const books = await res.json();
        renderBooks(books);
    } catch (err) { console.error("Error fetching books:", err); }
}

// Render table
function renderBooks(books) {
    const tbody = document.getElementById("booksTableBody");
    tbody.innerHTML = "";
    books.forEach(book => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${book.title}</td>
            <td>${book.author}</td>
            <td>${book.genre || '-'}</td>
            <td>${book.publisher || '-'}</td>
            <td>${book.year || '-'}</td>
            <td>${book.totalCopies}</td>
            <td>${book.availableCopies}</td>
            <td>${book.bookshelf || '-'}</td>
            <td><span class="${book.status === 'AVAILABLE' ? 'status-available' : 'status-unavailable'}">${book.status}</span></td>
            <td>
                <i class="fas fa-pen action-btn edit" onclick="openEditModal(${book.id})"></i>
                <i class="fas fa-trash action-btn delete" onclick="deleteBook(${book.id})"></i>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Add Book
document.getElementById("addBookForm").addEventListener("submit", async e => {
    e.preventDefault();
    const newBook = {
        title: document.getElementById("title").value,
        author: document.getElementById("author").value,
        genre: document.getElementById("genre").value,
        publisher: document.getElementById("publisher").value,
        year: parseInt(document.getElementById("year").value),
        isbn: document.getElementById("isbn").value,
        totalCopies: parseInt(document.getElementById("totalCopies").value),
        availableCopies: parseInt(document.getElementById("availableCopies").value),
        bookshelf: document.getElementById("bookshelf").value,
        status: "AVAILABLE"
    };
    try {
        const res = await fetch(`${API_BASE}/add`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(newBook)
        });
        if (res.ok) {
            alert("✅ Book added successfully!");
            e.target.reset();
            fetchBooks();
        } else { alert("❌ Failed to add book!"); }
    } catch (err) { console.error(err); }
});

// Edit Modal
async function openEditModal(id) {
    try {
        const res = await fetch(`${API_BASE}/${id}`);
        if (res.ok) {
            const book = await res.json();
            document.getElementById("editId").value = book.id;
            document.getElementById("editTitle").value = book.title;
            document.getElementById("editAuthor").value = book.author;
            document.getElementById("editGenre").value = book.genre;
            document.getElementById("editPublisher").value = book.publisher;
            document.getElementById("editYear").value = book.year;
            document.getElementById("editIsbn").value = book.isbn;
            document.getElementById("editTotalCopies").value = book.totalCopies;
            document.getElementById("editAvailableCopies").value = book.availableCopies;
            document.getElementById("editBookshelf").value = book.bookshelf;
            document.getElementById("editModal").style.display = "flex";
        }
    } catch (err) { console.error(err); }
}

function closeModal() { document.getElementById("editModal").style.display = "none"; }

// Update Book
document.getElementById("editBookForm").addEventListener("submit", async e => {
    e.preventDefault();
    const id = document.getElementById("editId").value;
    const updatedBook = {
        title: document.getElementById("editTitle").value,
        author: document.getElementById("editAuthor").value,
        genre: document.getElementById("editGenre").value,
        publisher: document.getElementById("editPublisher").value,
        year: parseInt(document.getElementById("editYear").value),
        isbn: document.getElementById("editIsbn").value,
        totalCopies: parseInt(document.getElementById("editTotalCopies").value),
        availableCopies: parseInt(document.getElementById("editAvailableCopies").value),
        bookshelf: document.getElementById("editBookshelf").value,
        status: "AVAILABLE"
    };
    try {
        const res = await fetch(`${API_BASE}/${id}`, {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(updatedBook)
        });
        if (res.ok) {
            alert("✅ Book updated successfully!");
            closeModal();
            fetchBooks();
        } else { alert("❌ Failed to update book!"); }
    } catch (err) { console.error(err); }
});

// Delete Book
async function deleteBook(id) {
    if (!confirm("Are you sure you want to delete this book?")) return;
    try {
        const res = await fetch(`${API_BASE}/${id}`, {method: "DELETE"});
        if (res.ok) { alert("✅ Book deleted successfully!"); fetchBooks(); }
        else alert("❌ Failed to delete book!");
    } catch (err) { console.error(err); }
}

// Search Books
async function searchBooks() {
    const query = document.getElementById("searchInput").value;
    if (query.trim() === "") { fetchBooks(); return; }
    try {
        const res = await fetch(`${API_BASE}/search?query=${query}`);
        if (res.ok) { const books = await res.json(); renderBooks(books); }
    } catch (err) { console.error(err); }
}

// Initial load
fetchBooks();
