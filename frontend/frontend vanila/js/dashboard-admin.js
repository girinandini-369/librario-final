// dashboard-admin.js

// Replace with your admin credentials
const adminEmail = "admin@librario.com";
const adminPassword = "Admin@123";

// Base64 encode for HTTP Basic Auth
const authHeader = "Basic " + btoa(adminEmail + ":" + adminPassword);

// ✅ Fetch overall admin dashboard stats
async function fetchDashboardSummary() {
    try {
        const response = await fetch("http://localhost:8080/api/admin/summary", {
            method: "GET",
            headers: {
                "Authorization": authHeader,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`Network response was not ok (${response.status})`);
        }

        const data = await response.json();
        console.log("Admin summary:", data);

        // Populate dashboard cards
        document.getElementById("totalBooks").textContent = data.totalBooks ?? 0;
        document.getElementById("totalUsers").textContent = data.totalUsers ?? 0;
        document.getElementById("booksBorrowed").textContent = data.booksBorrowed ?? 0;
        document.getElementById("activeBorrowRecords").textContent = data.activeBorrowRecords ?? 0;

    } catch (error) {
        console.error("Error fetching dashboard data:", error);

        // Optional: show error message on UI if you have a placeholder element
        const errorBox = document.getElementById("dashboardError");
        if (errorBox) {
            errorBox.textContent = "❌ Failed to load dashboard data. Please check your credentials or API.";
        }
    }
}

// Call function on page load
window.addEventListener("DOMContentLoaded", fetchDashboardSummary);
