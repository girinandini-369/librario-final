document.getElementById("resetForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const email = localStorage.getItem("resetEmail");
  const otp = localStorage.getItem("resetOtp");
  const newPassword = document.getElementById("newPassword").value;

  if (!email || !otp) {
    document.getElementById("msg").innerText = "❌ Session expired. Go back and request OTP again.";
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/api/auth/reset-password", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, otp, newPassword })
    });

    const text = await res.text();
    document.getElementById("msg").innerText = text;

    if (res.ok) {
      document.getElementById("msg").style.color = "green";
      setTimeout(() => window.location.href = "login.html", 2000);
    } else {
      document.getElementById("msg").style.color = "red";
    }
  } catch (err) {
    document.getElementById("msg").innerText = "⚠️ Server error";
    document.getElementById("msg").style.color = "red";
  }
});
