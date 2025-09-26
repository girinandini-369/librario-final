// transactions.js

const API_BASE = "http://localhost:8080/api/transactions";

// Fetch and render transactions
async function loadTransactions() {
    try {
        const response = await fetch(API_BASE);
        const data = await response.json();

        const tbody = document.getElementById("transactions-body");
        tbody.innerHTML = "";

        data.forEach(tx => {
            const tr = document.createElement("tr");

            tr.innerHTML = `
                <td>${tx.id}</td>
                <td>${tx.bookTitle || "-"}</td>
                <td>${tx.memberName || "-"}</td>
                <td>${tx.issueDate || "-"}</td>
                <td>${tx.dueDate || "-"}</td>
                <td>${tx.returnDate || "-"}</td>
                <td>${tx.fine || 0}</td>
                <td>${tx.status}</td>
                <td>
                    <button class="btn-return" onclick="markReturned(${tx.id})">Mark Returned</button>
                    <button class="btn-fine" onclick="markFinePaid(${tx.id})">Mark Fine Paid</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error("Error loading transactions:", error);
    }
}

// Mark as Returned
async function markReturned(id) {
    if (!confirm("Are you sure you want to mark this as returned?")) return;

    try {
        const response = await fetch(`${API_BASE}/${id}/mark-returned`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" }
        });

        if (response.ok) {
            alert("✅ Book marked as returned");
            loadTransactions();
        } else {
            alert("❌ Failed to mark returned");
        }
    } catch (error) {
        console.error("Error marking returned:", error);
    }
}

// Mark Fine Paid
async function markFinePaid(id) {
    if (!confirm("Are you sure you want to mark the fine as paid?")) return;

    try {
        const response = await fetch(`${API_BASE}/${id}/mark-fine-paid`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" }
        });

        if (response.ok) {
            alert("✅ Fine marked as paid");
            loadTransactions();
        } else {
            alert("❌ Failed to mark fine as paid");
        }
    } catch (error) {
        console.error("Error marking fine paid:", error);
    }
}

// Load on page load
document.addEventListener("DOMContentLoaded", loadTransactions);
