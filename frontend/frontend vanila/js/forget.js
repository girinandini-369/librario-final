document.getElementById("forgotForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const email = document.getElementById("email").value;

  const res = await fetch("http://localhost:8080/api/auth/forgot-password", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email })
  });

  const text = await res.text();
  document.getElementById("msg").innerText = text;

  if (res.ok) {
    localStorage.setItem("resetEmail", email); // save for OTP
    setTimeout(() => window.location.href = "otp.html", 1500);
  }
});
