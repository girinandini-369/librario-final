document.getElementById("librarianForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
        const response = await fetch("http://localhost:8080/api/admin/librarian", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Basic " + btoa("admin@librario.com:Admin@123")
            },
            body: JSON.stringify({ name, email, password })
        });

        const data = await response.json();
        document.getElementById("message").textContent = data.message || JSON.stringify(data);
    } catch (err) {
        document.getElementById("message").textContent = "Error: " + err.message;
    }
});
