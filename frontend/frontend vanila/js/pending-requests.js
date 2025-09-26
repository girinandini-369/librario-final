const API_BASE = "http://localhost:8080/api/borrow-records";
const tbody = document.getElementById("pendingTableBody");

document.addEventListener("DOMContentLoaded", loadPending);

async function loadPending() {
  tbody.innerHTML = `<tr><td colspan="7" class="text-center">Loading...</td></tr>`;
  try {
    const res = await fetch(`${API_BASE}/pending`);
    if (!res.ok) throw new Error(`Failed: ${res.status}`);
    const data = await res.json();

    if (!Array.isArray(data) || data.length === 0) {
      tbody.innerHTML = `<tr><td colspan="7" class="text-center">No pending requests</td></tr>`;
      return;
    }

    tbody.innerHTML = "";
    data.forEach(req => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${req.id ?? ""}</td>
        <td>${req.memberId ?? ""}</td>
        <td>${escapeHtml(req.memberName ?? "Unknown")}</td>
        <td>${escapeHtml(req.bookTitle ?? "Unknown")}</td>
        <td>${req.borrowDate ? new Date(req.borrowDate).toLocaleDateString() : "-"}</td>
        <td><span class="badge bg-warning text-dark">${req.status ?? "PENDING"}</span></td>
        <td>
          <button class="btn btn-success btn-sm me-1" data-action="approve" data-id="${req.id}">Approve</button>
          <button class="btn btn-danger btn-sm me-1" data-action="reject" data-id="${req.id}">Reject</button>
        </td>
      `;
      tbody.appendChild(tr);
    });

    tbody.querySelectorAll("button[data-action]").forEach(btn =>
      btn.addEventListener("click", onAction)
    );

  } catch (err) {
    console.error("Error loading pending requests:", err);
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">Failed to load requests</td></tr>`;
  }
}

async function onAction(e) {
  const id = e.currentTarget.dataset.id;
  const action = e.currentTarget.dataset.action;
  if (!id || !action) return;

  let endpoint = "";
  if (action === "approve") endpoint = `${API_BASE}/${id}/approve`;
  if (action === "reject") endpoint = `${API_BASE}/${id}/reject`;

  if (!endpoint) return;

  if (!confirm(`Are you sure you want to ${action} this request?`)) return;

  try {
    const res = await fetch(endpoint, { method: "PUT" });
    const data = await res.json();
    if (!res.ok) throw new Error(data?.message || "Action failed");

    alert(`Request ${action}d successfully!`);
    await loadPending(); // reload table
  } catch (err) {
    console.error(`Error on ${action}:`, err);
    alert(`Failed to ${action} request`);
  }
}

function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}
