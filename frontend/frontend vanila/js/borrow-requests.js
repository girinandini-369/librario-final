// borrow-requests.js
// Place in your js/ folder and include in borrow-requests.html
// Expected container in HTML: <tbody id="requestsTableBody"></tbody>

const API_BASE = "http://localhost:8080"; // change if needed
const ENDPOINTS = {
  listPending: `${API_BASE}/api/requests/pending`,
  approve: (id) => `${API_BASE}/api/requests/approve/${id}`, // POST
  reject: (id) => `${API_BASE}/api/requests/reject/${id}`   // POST
};

document.addEventListener("DOMContentLoaded", loadRequests);

async function loadRequests() {
  const tbody = document.getElementById("requestsTableBody");
  if (!tbody) {
    console.error("requestsTableBody element not found in DOM.");
    return;
  }
  tbody.innerHTML = `<tr><td colspan="6">Loading...</td></tr>`;

  try {
    const res = await fetch(ENDPOINTS.listPending);
    const data = await safeParseJson(res);

    if (!Array.isArray(data)) {
      console.warn("Expected array for pending requests, got:", data);
      tbody.innerHTML = `<tr><td colspan="6">No requests (or backend returned unexpected response). Check console.</td></tr>`;
      return;
    }

    if (data.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6">No pending requests</td></tr>`;
      return;
    }

    tbody.innerHTML = "";
    data.forEach((r, idx) => {
      const tr = document.createElement("tr");

      const reqDate = r.requestDate ? new Date(r.requestDate).toLocaleString() : (r.borrowDate ? new Date(r.borrowDate).toLocaleString() : "-");
      tr.innerHTML = `
        <td>${r.id ?? ""}</td>
        <td>${escapeHtml(r.memberName ?? (r.member?.name ?? "Unknown"))}</td>
        <td>${escapeHtml(r.bookTitle ?? (r.book?.title ?? "Unknown"))}</td>
        <td>${reqDate}</td>
        <td>${r.status ?? "PENDING"}</td>
        <td>
          <button class="btn-approve" data-id="${r.id}">Approve</button>
          <button class="btn-reject" data-id="${r.id}">Reject</button>
        </td>
      `;
      tbody.appendChild(tr);
    });

    // attach handlers
    tbody.querySelectorAll(".btn-approve").forEach(b => b.addEventListener("click", onApprove));
    tbody.querySelectorAll(".btn-reject").forEach(b => b.addEventListener("click", onReject));

  } catch (err) {
    console.error("Error loading borrow requests:", err);
    tbody.innerHTML = `<tr><td colspan="6">Failed to load pending requests. See console.</td></tr>`;
  }
}

async function onApprove(e) {
  const id = e.currentTarget.dataset.id;
  if (!confirm("Approve this request?")) return;
  await doAction(ENDPOINTS.approve(id), "POST", null, "Request approved");
  await loadRequests();
}
async function onReject(e) {
  const id = e.currentTarget.dataset.id;
  if (!confirm("Reject this request?")) return;
  await doAction(ENDPOINTS.reject(id), "POST", null, "Request rejected");
  await loadRequests();
}

/* ----- helpers ----- */

async function doAction(url, method = "POST", body = null, successMsg = null) {
  try {
    const res = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: body ? JSON.stringify(body) : undefined
    });
    const parsed = await safeParseJson(res);
    if (!res.ok) {
      console.error("Action failed:", parsed);
      alert("Action failed. See console.");
      return null;
    }
    if (successMsg) alert(successMsg);
    return parsed;
  } catch (err) {
    console.error("Action error:", err);
    alert("Action failed. See console.");
    return null;
  }
}

// try to parse possible JSON or return text fallback
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
