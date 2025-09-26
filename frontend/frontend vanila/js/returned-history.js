document.addEventListener("DOMContentLoaded", () => {
  const memberId = localStorage.getItem("memberId");
  const role = (localStorage.getItem("role") || "").toUpperCase();

  // ✅ Only members allowed
  if (!memberId || role !== "MEMBER") {
    alert("⚠️ Unauthorized access. Please login as a member.");
    window.location.href = "login.html";
    return;
  }

  loadReturnedHistory(memberId);

  // Logout
  document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "login.html";
  });
});

// ✅ Fetch returned books
function loadReturnedHistory(memberId) {
  fetch(`http://localhost:8080/api/transactions/member/${memberId}/returned`)
    .then(handleResponse)
    .then(data => renderBooks("returnedContainer", data))
    .catch(err => showError("returnedContainer", err));
}

// ✅ Handle API response safely
function handleResponse(res) {
  if (!res.ok) {
    throw new Error(`Server error: ${res.status}`);
  }
  return res.json();
}

// ✅ Render returned books
function renderBooks(containerId, records) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";

  if (!Array.isArray(records) || records.length === 0) {
    container.innerHTML = `<p>No books returned yet.</p>`;
    return;
  }

  records.forEach(r => {
    const card = document.createElement("div");
    card.className = "book-card";

    card.innerHTML = `
      <h3>${r.bookTitle || "Untitled"}</h3>
      <p><strong>Author:</strong> ${r.bookAuthor || "Unknown"}</p>
      <p><strong>Issue Date:</strong> ${r.issueDate || "-"}</p>
      <p><strong>Due Date:</strong> ${r.dueDate || "-"}</p>
      <p><strong>Return Date:</strong> ${r.returnDate || "-"}</p>
      ${r.fineAmount && r.fineAmount > 0 ? `<p><strong>Fine Paid:</strong> ₹${r.fineAmount}</p>` : ""}
      <p class="status">✅ Returned</p>
    `;

    container.appendChild(card);
  });
}

// ❌ Error handler
function showError(containerId, err) {
  console.error("Error loading returned history:", err);
  document.getElementById(containerId).innerHTML =
    `<p style="color:red;">❌ Failed to load returned history.</p>`;
}
