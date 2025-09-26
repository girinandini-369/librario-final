const baseUrl = "http://localhost:8080/api/borrow-records";
const memberId = 9; // 👉 Replace with logged-in member ID

document.addEventListener("DOMContentLoaded", () => {
  loadBorrowed();
  loadOverdue();
  loadPending();
  loadReturned();

  // Tab switching
  document.querySelectorAll(".tab-button").forEach(button => {
    button.addEventListener("click", () => {
      document.querySelectorAll(".tab-button").forEach(btn => btn.classList.remove("active"));
      document.querySelectorAll(".tab-content").forEach(tab => tab.classList.remove("active"));
      button.classList.add("active");
      document.getElementById(button.dataset.tab).classList.add("active");
    });
  });
});

async function fetchData(url) {
  try {
    const res = await fetch(url);
    return await res.json();
  } catch (err) {
    console.error("❌ API Error:", err);
    return [];
  }
}

function renderList(containerId, records, filterStatus = null) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";

  const filtered = filterStatus
    ? records.filter(r => r.status === filterStatus)
    : records;

  if (!filtered.length) {
    container.innerHTML = `<p>No books found.</p>`;
    return;
  }

  filtered.forEach(r => {
    const card = document.createElement("div");
    card.className = "card";
    card.innerHTML = `
      <h3>${r.bookTitle}</h3>
      <p><strong>Member:</strong> ${r.memberName}</p>
      <p><strong>Borrowed:</strong> ${r.borrowDate || "—"}</p>
      <p><strong>Due:</strong> ${r.dueDate || "—"}</p>
      <p><strong>Returned:</strong> ${r.returnDate || "—"}</p>
      <span class="status ${r.status}">${r.status}</span>
    `;
    container.appendChild(card);
  });
}

// 📌 Borrowed Books
async function loadBorrowed() {
  const data = await fetchData(`${baseUrl}/member/${memberId}`);
  renderList("borrowedList", data, "BORROWED");
}

// 📌 Overdue Books
async function loadOverdue() {
  const data = await fetchData(`${baseUrl}/member/${memberId}/overdue`);
  renderList("overdueList", data);
}

// 📌 Pending Requests
async function loadPending() {
  const data = await fetchData(`${baseUrl}/member/${memberId}/pending`);
  renderList("pendingList", data);
}

// 📌 Returned Books
async function loadReturned() {
  const data = await fetchData(`${baseUrl}/member/${memberId}`);
  renderList("returnedList", data, "RETURNED");
}
