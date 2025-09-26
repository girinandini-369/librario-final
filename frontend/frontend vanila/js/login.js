document.getElementById("loginForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    })
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            alert(data.message);
            return;
        }

        alert(data.message); // ✅ show login success

        // Save session info
        localStorage.setItem("memberId", data.id || data.memberId); 
        localStorage.setItem("role", data.role);

        // Redirect based on role
        const role = (data.role || "").toUpperCase();

        if (role === "ADMIN") {
            window.location.href = "dashboard-admin.html";
        } else if (role === "LIBRARIAN") {
            window.location.href = "dashboard-librarian.html";
        } else {
            window.location.href = "dashboard-user.html";
        }
    })
    .catch(error => {
        console.error("Login failed:", error);
        alert("Login failed. Please try again.");
    });
});
