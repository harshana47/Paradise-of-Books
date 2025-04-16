let userEmail = "";
let countdownTimer;

async function sendOTP() {
    const email = document.getElementById("email").value;
    if (!email.includes("@")) {
        document.getElementById("email-error").innerText = "Enter a valid email!";
        return;
    }

    const response = await fetch("http://localhost:8080/api/v1/auth/forgot-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email })
    });

    const result = await response.json();
    if (result.success) {
        userEmail = email;
        nextStep();
        startCountdown();
    } else {
        document.getElementById("email-error").innerText = "Email not found!";
    }
}

async function resetPassword() {
    const password = document.getElementById("new-password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    if (password !== confirmPassword) {
        document.getElementById("password-error").innerText = "Passwords do not match!";
        return;
    }

    const response = await fetch("http://localhost:8080/api/v1/auth/reset-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: userEmail, password })
    });

    const result = await response.json();
    if (result.success) {
        alert("Password changed successfully!");
        window.location.href = "../../index.html";
    } else {
        document.getElementById("password-error").innerText = "Error resetting password!";
    }
}

function startCountdown() {
    let timeLeft = 30;
    const countdownElement = document.getElementById("countdown");
    const resendButton = document.getElementById("resend-btn");

    resendButton.style.display = "none";
    countdownElement.innerText = `Resend OTP in ${timeLeft}s`;

    countdownTimer = setInterval(() => {
        timeLeft--;
        countdownElement.innerText = `Resend OTP in ${timeLeft}s`;

        if (timeLeft <= 0) {
            clearInterval(countdownTimer);
            countdownElement.innerText = "";
            resendButton.style.display = "block";
        }
    }, 1000);
}

async function resendOTP() {
    if (!userEmail) {
        document.getElementById("email-error").innerText = "Please enter your email first!";
        return;
    }

    const response = await fetch("http://localhost:8080/api/v1/auth/resend-otp", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: userEmail })
    });

    const result = await response.json();
    if (result.success) {
        Swal.fire("Success", "A new OTP has been sent!", "success");
        startCountdown();
    } else {
        document.getElementById("otp-error").innerText = "Error resending OTP!";
    }
}
async function verifyOTP() {
    const otp = document.getElementById("otp").value;

    const response = await fetch("http://localhost:8080/api/v1/auth/verify-otp", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: userEmail, otp: otp })
    });

    const result = await response.json();
    if (result.success) {
        nextStep();
    } else {
        document.getElementById("otp-error").innerText = result.message || "Invalid OTP!";
    }
}
function nextStep() {
    const currentStep = document.querySelector(".form-step.active");
    currentStep.classList.remove("active");
    currentStep.nextElementSibling.classList.add("active");
}