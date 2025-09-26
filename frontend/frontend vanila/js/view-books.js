const tableBody = document.getElementById("books-table-body");
const messageDiv = document.getElementById("message");
const API_URL = "http://localhost:8080/api/books";

// Replace with your admin credentials
const authHeader = "Basic " + btoa("admin@librario.com:Admin@123");

// Fetch all books and display
async function loadBooks() {
    try {
        const res = await fetch(API_URL, {
            headers: {
                "Authorization": authHeader,
                "Content-Type": "application/json"
            }
        });
        const books = await res.json();
        tableBody.innerHTML = "";
        books.forEach(book => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td><span class="text">${book.title}</span><input type="text" class="edit-input" value="${book.title}" style="display:none;"></td>
                <td><span class="text">${book.author}</span><input type="text" class="edit-input" value="${book.author}" style="display:none;"></td>
                <td><span class="text">${book.genre || ''}</span><input type="text" class="edit-input" value="${book.genre || ''}" style="display:none;"></td>
                <td><span class="text">${book.publisher || ''}</span><input type="text" class="edit-input" value="${book.publisher || ''}" style="display:none;"></td>
                <td><span class="text">${book.year || ''}</span><input type="number" class="edit-input" value="${book.year || ''}" style="display:none;"></td>
                <td><span class="text">${book.isbn || ''}</span><input type="text" class="edit-input" value="${book.isbn || ''}" style="display:none;"></td>
                <td><span class="text">${book.bookshelf || ''}</span><input type="text" class="edit-input" value="${book.bookshelf || ''}" style="display:none;"></td>
                <td>
                    <i class="fas fa-pencil-alt action-btn edit"></i>
                    <i class="fas fa-floppy-disk action-btn save" style="display:none;"></i>
                    <i class="fas fa-trash action-btn delete"></i>
                </td>
            `;

            const editBtn = row.querySelector(".edit");
            const saveBtn = row.querySelector(".save");
            const deleteBtn = row.querySelector(".delete");
            const textSpans = row.querySelectorAll(".text");
            const inputs = row.querySelectorAll(".edit-input");

            // Edit button
            editBtn.addEventListener("click", () => {
                textSpans.forEach(span => span.style.display = "none");
                inputs.forEach(input => input.style.display = "block");
                editBtn.style.display = "none";
                saveBtn.style.display = "inline-block";
            });

            // Save button
            saveBtn.addEventListener("click", async () => {
                const updatedBook = {
                    title: inputs[0].value.trim(),
                    author: inputs[1].value.trim(),
                    genre: inputs[2].value.trim(),
                    publisher: inputs[3].value.trim(),
                    year: parseInt(inputs[4].value) || null,
                    isbn: inputs[5].value.trim(),
                    bookshelf: inputs[6].value.trim(),
                    totalCopies: book.totalCopies,
                    availableCopies: book.availableCopies,
                    status: book.status
                };

                try {
                    const res = await fetch(`${API_URL}/${book.id}`, {
                        method: "PUT",
                        headers: {
                            "Authorization": authHeader,
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(updatedBook)
                    });
                    const data = await res.json();
                    if(res.ok){
                        messageDiv.textContent = "✅ Book updated successfully!";
                        loadBooks();
                    } else {
                        messageDiv.textContent = data.message || "❌ Update failed";
                    }
                } catch(err) {
                    messageDiv.textContent = "❌ Error: " + err.message;
                }
            });

            // Delete button
            deleteBtn.addEventListener("click", async () => {
                if(confirm("Are you sure you want to delete this book?")){
                    try {
                        const res = await fetch(`${API_URL}/${book.id}`, {
                            method: "DELETE",
                            headers: { "Authorization": authHeader }
                        });
                        if(res.ok){
                            messageDiv.textContent = "✅ Book deleted successfully!";
                            loadBooks();
                        } else {
                            const data = await res.json();
                            messageDiv.textContent = data.message || "❌ Delete failed";
                        }
                    } catch(err) {
                        messageDiv.textContent = "❌ Error: " + err.message;
                    }
                }
            });

            tableBody.appendChild(row);
        });
    } catch (err) {
        tableBody.innerHTML = `<tr><td colspan="8">Error loading books: ${err.message}</td></tr>`;
    }
}

// Load books on page load
loadBooks();
