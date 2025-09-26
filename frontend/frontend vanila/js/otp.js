document.getElementById("otpForm").addEventListener("submit", (e) => {
  e.preventDefault();

  const otp = document.getElementById("otp").value.trim();

  if (!otp) {
    document.getElementById("msg").innerText = "❌ Please enter OTP";
    document.getElementById("msg").style.color = "red";
    return;
  }

  // Save OTP in local storage for reset step
  localStorage.setItem("resetOtp", otp);

  document.getElementById("msg").innerText = "✅ OTP captured, proceed to reset password";
  document.getElementById("msg").style.color = "green";

  setTimeout(() => window.location.href = "reset.html", 1500);
});
