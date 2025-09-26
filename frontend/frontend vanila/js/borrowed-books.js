const API_BASE = "http://localhost:8080";
const ENDPOINTS_BORROWED = {
  listBorrowed: `${API_BASE}/api/borrow-records`,
  returnAction: (id) => `${API_BASE}/api/borrow-records/${id}/return`,
  renewAction: `${API_BASE}/api/transactions/renew`, // POST expects { transactionId, extraDays }
  getTransactionsByBook: (bookId) => `${API_BASE}/api/transactions/book/${bookId}`
};

document.addEventListener("DOMContentLoaded", () => {
  console.log("DOM ready, loading borrowed books...");
  loadBorrowed();
});

async function loadBorrowed() {
  const tbody = document.getElementById("borrowedTableBody");
  if (!tbody) {
    console.error("borrowedTableBody element not found in DOM.");
    return;
  }
  tbody.innerHTML = `<tr><td colspan="7">Loading...</td></tr>`;

  try {
    const res = await fetch(ENDPOINTS_BORROWED.listBorrowed);
    if (!res.ok) throw new Error("Failed to fetch borrowed records");
    const data = await safeParseJson(res);

    let arr = Array.isArray(data) ? data : [];
    arr = arr.filter(tx => tx.status === "BORROWED");

    if (arr.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="text-center">No borrowed books</td></tr>`;
      return;
    }

    tbody.innerHTML = "";
    arr.forEach(tx => {
      const borrowDate = tx.borrowDate;
      const dueDate = tx.dueDate;
      const tr = document.createElement("tr");

      tr.innerHTML = `
        <td>${tx.transactionId ?? tx.id ?? ""}</td>
        <td>${escapeHtml(tx.memberName ?? "Unknown")}</td>
        <td>${escapeHtml(tx.bookTitle ?? "Unknown")}</td>
        <td>${borrowDate ? new Date(borrowDate).toLocaleDateString() : "-"}</td>
        <td>${dueDate ? new Date(dueDate).toLocaleDateString() : "-"}</td>
        <td><span class="badge bg-primary">${tx.status ?? "BORROWED"}</span></td>
        <td>
          <button class="btn btn-success btn-sm me-1 btn-return" 
                  data-id="${tx.id}">Return</button>
          <button class="btn btn-warning btn-sm btn-renew" 
                  data-transaction-id="${tx.transactionId ?? ""}" 
                  data-book-id="${tx.bookId ?? ""}"
                  data-id="${tx.id}">Renew</button>
        </td>
      `;
      tbody.appendChild(tr);
    });

    tbody.querySelectorAll(".btn-return").forEach(b => b.addEventListener("click", onReturn));
    tbody.querySelectorAll(".btn-renew").forEach(b => b.addEventListener("click", onRenew));

  } catch (err) {
    console.error("Error loading borrowed books:", err);
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">Failed to load. See console.</td></tr>`;
  }
}

async function onReturn(e) {
  const id = e.currentTarget.dataset.id;
  if (!id) return alert("Missing record id");
  if (!confirm("Mark this book as returned?")) return;

  try {
    const res = await fetch(ENDPOINTS_BORROWED.returnAction(id), { method: "PUT" });
    if (!res.ok) throw new Error("Failed to return book");
    alert("Book marked as returned ✅");
    await loadBorrowed();
  } catch (err) {
    console.error("Return action failed:", err);
    alert("CHECK MAIL.");
  }
}

async function onRenew(e) {
  let transactionId = e.currentTarget.dataset.transactionId;
  const bookId = e.currentTarget.dataset.bookId;

  // If transactionId missing, fetch via bookId
  if (!transactionId && bookId) {
    try {
      const res = await fetch(ENDPOINTS_BORROWED.getTransactionsByBook(bookId));
      const txList = await safeParseJson(res);
      if (Array.isArray(txList) && txList.length > 0) {
        transactionId = txList[0].id; // take first transaction
        console.log("Resolved transactionId via book:", transactionId);
      }
    } catch (err) {
      console.error("Failed to resolve transactionId via book:", err);
    }
  }

  if (!transactionId) return alert("Missing transaction id");

  const extraDays = prompt("Enter number of extra days for renewal:", "7");
  if (!extraDays || isNaN(extraDays)) return alert("Invalid number of days");

  try {
    const res = await fetch(ENDPOINTS_BORROWED.renewAction, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ 
        transactionId: Number(transactionId), 
        extraDays: Number(extraDays) 
      })
    });
    const data = await safeParseJson(res);
    if (!res.ok) throw new Error(data?.message || "Failed to renew");

    alert("Book renewed successfully ✅");
    await loadBorrowed();
  } catch (err) {
    console.error("Renew action failed:", err);
    alert("CHECK MAIL.");
  }
}

/* helpers */
async function safeParseJson(res) {
  const t = await res.text();
  try {
    return JSON.parse(t || "null");
  } catch (e) {
    return t;
  }
}

function escapeHtml(str) {
  if (!str && str !== 0) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}
