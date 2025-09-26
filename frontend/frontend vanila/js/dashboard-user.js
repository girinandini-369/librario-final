document.addEventListener("DOMContentLoaded", () => {
  fetchBooks();
});

// ✅ Map backend titles to images
function getBookImage(title) {
  const map = {
    "Clean Code": "images/cc.png",
    "Clean Code Updated": "images/ccu.png",
    "Domain-Driven Design": "images/ddd.png",
    "Harry Potter": "images/hp.png",
    "The Pragmatic Programmer": "images/tpp.png"
  };
  return map[title] || "images/cuu.png";
}

function fetchBooks() {
  fetch("http://localhost:8080/api/books")
    .then(res => {
      if (!res.ok) throw new Error("Failed to fetch books: " + res.status);
      return res.json();
    })
    .then(books => renderBooks(books))
    .catch(err => {
      console.error(err);
      document.getElementById("bookGrid").innerHTML =
        `<p style="color:red;">❌ Failed to load books</p>`;
    });
}

function renderBooks(books) {
  const grid = document.getElementById("bookGrid");
  grid.innerHTML = "";

  if (!books.length) {
    grid.innerHTML = "<p>No books available.</p>";
    return;
  }

  books.forEach(book => {
    const card = document.createElement("div");
    card.className = "book-card";
    card.innerHTML = `
      <img src="${getBookImage(book.title)}" alt="${book.title}" 
           onerror="this.src='images/default.png'">
      <div class="content">
        <h3>${book.title}</h3>
        <p><strong>Author:</strong> ${book.author}</p>
        <p><strong>Genre:</strong> ${book.genre}</p>
        <p><strong>Available Copies:</strong> ${book.availableCopies ?? 0}</p>
        <button onclick="requestBook(${book.id})">Request</button>
      </div>
    `;
    grid.appendChild(card);
  });
}

function requestBook(bookId) {
  const memberId = localStorage.getItem("memberId");

  if (!memberId) {
    alert("⚠️ Session expired. Please login again.");
    window.location.href = "login.html";
    return;
  }

  fetch("http://localhost:8080/api/borrow-records/request", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ memberId: parseInt(memberId), bookId: bookId })
  })
    .then(res => {
      if (!res.ok) {
        throw new Error("Failed to request book: " + res.status);
      }
      return res.json();
    })
    .then(data => {
      alert("✅ Book requested successfully!");
      console.log("Borrow record:", data);
    })
    .catch(err => {
      console.error(err);
      alert("❌ Failed to request book.");
    });
}

function logout() {
  localStorage.removeItem("memberId");
  localStorage.removeItem("role");
  localStorage.removeItem("email");
  window.location.href = "login.html";
}
