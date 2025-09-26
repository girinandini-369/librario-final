// payments-admin-full.js
const API_BASE = "http://localhost:8080/api";

async function fetchPayments() {
  const tBody = document.querySelector("#paymentsTable tbody");
  const loading = document.getElementById("loading");
  try {
    loading.style.display = "block";
    const res = await fetch(`${API_BASE}/payments/all`);
    if (!res.ok) throw new Error("Failed to fetch payments: " + res.status);
    const data = await res.json();
    tBody.innerHTML = "";
    if (!Array.isArray(data) || data.length === 0) {
      tBody.innerHTML = `<tr><td colspan="9" class="muted">No payments found.</td></tr>`;
      return;
    }

    data.forEach((p, idx) => {
      // Member name resolution: try several possible paths
      let memberName = "Unknown";
      try {
        if (p.transaction && p.transaction.member) {
          const m = p.transaction.member;
          memberName = m.name || m.userName || (m.user && (m.user.name || m.user.email)) || memberName;
        } else if (p.transaction && p.transaction.memberName) {
          memberName = p.transaction.memberName;
        } else if (p.member && (p.member.name || p.member.userName)) {
          memberName = p.member.name || p.member.userName;
        }
      } catch (e) { /* ignore */ }

      const amount = (() => {
        if (p.amount == null) return "-";
        // backend stores paise — attempt conversion; but support rupee value too
        const val = Number(p.amount);
        if (val > 1000) return (val / 100).toFixed(2);
        return val % 1 === 0 ? val.toFixed(2) : val.toString();
      })();

      const created = p.createdAt ? new Date(p.createdAt).toLocaleString() : (p.created_at || "-");
      const paymentId = p.paymentId || p.payment_id || p.id || "-";
      const orderId = p.orderId || p.order_id || "-";
      const txId = p.transaction ? (p.transaction.id || p.transactionId || "-") : (p.transactionId || "-");

      tBody.insertAdjacentHTML("beforeend", `
        <tr>
          <td>${idx+1}</td>
          <td>${paymentId}</td>
          <td>${escapeHtml(memberName)}</td>
          <td style="font-weight:600;">${amount}</td>
          <td>${p.currency || "-"}</td>
          <td>${p.status || "-"}</td>
          <td>${orderId}</td>
          <td>${txId}</td>
          <td>${created}</td>
        </tr>
      `);
    });

  } catch (err) {
    console.error(err);
    document.querySelector("#paymentsTable tbody").innerHTML = `<tr><td colspan="9" class="muted">Error loading payments: ${escapeHtml(err.message)}</td></tr>`;
  } finally {
    loading.style.display = "none";
  }
}

function escapeHtml(s) {
  if (s == null) return "";
  return String(s)
    .replaceAll("&","&amp;")
    .replaceAll("<","&lt;")
    .replaceAll(">","&gt;")
    .replaceAll('"',"&quot;");
}

document.addEventListener("DOMContentLoaded", () => {
  fetchPayments();
  document.getElementById("refreshBtn").addEventListener("click", fetchPayments);
  document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear(); sessionStorage.clear(); window.location.href = "login.html";
  });
});
