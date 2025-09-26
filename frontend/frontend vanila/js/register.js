document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("registerForm");
  const messageEl = document.getElementById("message");

  form.addEventListener("submit", async function (e) {
    e.preventDefault();

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {
      messageEl.style.color = "red";
      messageEl.textContent = "❌ Passwords do not match!";
      return;
    }

    // Prepare user object with membership = null
    const userPayload = {
      name,
      email,
      password,
      membership: null // 🚀 ensures admin later assigns Basic/Premium
    };

    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userPayload)
      });

      const result = await response.json();

      if (response.ok) {
        messageEl.style.color = "green";
        messageEl.textContent = "✅ " + (result.message || "Registration successful!");
        setTimeout(() => {
          window.location.href = "login.html";
        }, 1500);
      } else {
        messageEl.style.color = "red";
        messageEl.textContent = "❌ " + (result.message || "Registration failed!");
      }
    } catch (error) {
      console.error("Error:", error);
      messageEl.style.color = "red";
      messageEl.textContent = "❌ Server error. Please try again later.";
    }
  });
});
