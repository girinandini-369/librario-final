document.getElementById("bookForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const bookData = {
        title: document.getElementById("title").value.trim(),
        author: document.getElementById("author").value.trim(),
        genre: document.getElementById("genre").value.trim(),
        publisher: document.getElementById("publisher").value.trim(),
        year: Number(document.getElementById("year").value),
        isbn: document.getElementById("isbn").value.trim(),
        totalCopies: Number(document.getElementById("totalCopies").value),
        availableCopies: Number(document.getElementById("totalCopies").value),
        bookshelf: document.getElementById("bookshelf").value.trim(),
        status: "AVAILABLE"
    };

    try {
        const response = await fetch("http://localhost:8080/api/books/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Basic " + btoa("admin@librario.com:Admin@123")
            },
            body: JSON.stringify(bookData)
        });

        const data = await response.json();
        document.getElementById("message").textContent = data.title ? "✅ Book added successfully!" : data.message || JSON.stringify(data);
        if(data.title) document.getElementById("bookForm").reset();
    } catch (err) {
        document.getElementById("message").textContent = "Error: " + err.message;
    }
});
