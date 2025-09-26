document.addEventListener("DOMContentLoaded", () => {
  fetchDashboardSummary();
  fetchPendingRequests();
});

// ✅ Fetch overall dashboard stats
function fetchDashboardSummary() {
  Promise.all([
    fetch("http://localhost:8080/api/books").then(res => res.json()),
    fetch("http://localhost:8080/api/members").then(res => res.json()),
    fetch("http://localhost:8080/api/borrow-records").then(res => res.json())
  ])
    .then(([books, users, transactions]) => {
      document.getElementById("totalBooks").textContent = books.length;
      document.getElementById("totalUsers").textContent = users.length;
      document.getElementById("booksBorrowed").textContent =
        transactions.filter(tx => tx.status === "BORROWED").length;
    })
    .catch(err => {
      console.error("Error fetching summary:", err);
    });
}

// ✅ Fetch pending borrow requests and update the counter
function fetchPendingRequests() {
  fetch("http://localhost:8080/api/borrow-records/pending")
    .then(res => {
      if (!res.ok) throw new Error("Failed to fetch pending requests: " + res.status);
      return res.json();
    })
    .then(requests => {
      document.getElementById("pendingRequests").textContent = requests.length;
    })
    .catch(err => {
      console.error(err);
      document.getElementById("pendingRequests").textContent = "❌";
    });
}

// ✅ Logout
function logout() {
  localStorage.removeItem("memberId");
  window.location.href = "login.html";
}
