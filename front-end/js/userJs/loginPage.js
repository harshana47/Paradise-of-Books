const loginText = document.querySelector(".title-text .login");
const loginForm = document.querySelector("form.login");
const loginBtn = document.querySelector("label.login");
const signupBtn = document.querySelector("label.signup");
const signupLink = document.querySelector("form .signup-link a");
signupBtn.onclick = (()=>{
    loginForm.style.marginLeft = "-50%";
    loginText.style.marginLeft = "-50%";
});
loginBtn.onclick = (()=>{
    loginForm.style.marginLeft = "0%";
    loginText.style.marginLeft = "0%";
});
signupLink.onclick = (()=>{
    signupBtn.click();
    return false;
});
$('#submit-btn').on('click', function(event) {
    event.preventDefault();

    let email = $('#email').val();
    let password = $('#password').val();

    if (!email || !password) {
        alert("Please enter both email and password.");
        return;
    }

    let loginUser = {
        'email': email,
        'password': password,
    };

    $.ajax({
        url: `http://localhost:8080/api/v1/auth/authenticate`,
        method: `POST`,
        headers: {
            "Content-Type": "application/json"
        },
        data: JSON.stringify(loginUser),
        success: function(response) {
            console.log("Login Response:", response);
            if (response.code === 201 && response.data && response.data.token) {
                let token = response.data.token;
                let userEmail = response.data.email;
                let userRole = response.data.role;

                localStorage.setItem("authToken", token);
                localStorage.setItem("userEmail", userEmail);
                localStorage.setItem("userRole", userRole);
                console.log("role "+userRole)
                console.log("Token and User Email saved:", token, userEmail);

                setTimeout(() => {
                    getUserIdFromEmail(userEmail, token);
                }, 1000);
            } else {
                console.error("No token received in response.");
                localStorage.removeItem("authToken");
                localStorage.removeItem("userEmail");
                alert("Login failed! Invalid credentials.");
            }
        },
        error: function(xhr) {
            console.log("Login failed:", xhr.responseText);
            localStorage.removeItem("authToken");
            localStorage.removeItem("userEmail");
            alert("Login failed! Please check your credentials.");
        }
    });
});

function getUserIdFromEmail(email, token) {
    $.ajax({
        url: `http://localhost:8080/api/v1/user/findByEmail`,
        method: `GET`,
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        data: { "email": email },
        success: function(response) {
            console.log("User Data:", response);
            if (response.code === 200 && response.data) {
                let userId = response.data.uid;
                localStorage.setItem("userId", userId);
                console.log("User ID saved:", userId);

                setTimeout(() => {
                    checkUserRole(token);
                }, 1000);
            } else {
                console.error("User not found.");
                localStorage.removeItem("authToken");
                localStorage.removeItem("userEmail");
                alert("User not found. Please check your credentials.");
            }
        },
        error: function(xhr) {
            console.log("Error fetching userId:", xhr.responseText);
            localStorage.removeItem("authToken");
            localStorage.removeItem("userEmail");
            alert("Error retrieving userId! Please try again.");
        }
    });
}

function checkUserRole(token) {
    console.log("Token for role check:", token);

    $.ajax({
        url: `http://localhost:8080/api/v1/admin/test1`,
        method: `GET`,
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        success: function() {
            window.location.href = "admin/adminDashBoard.html";
        },
        error: function(xhr) {
            console.log("Error on test1:", xhr.responseText);
            $.ajax({
                url: `http://localhost:8080/api/v1/admin/test2`,
                method: `GET`,
                headers: {
                    "Authorization": "Bearer " + token,
                    "Content-Type": "application/json"
                },
                success: function() {
                    window.location.href = "user/homePage.html";
                },
                error: function(xhr) {
                    console.log("Error on test2:", xhr.responseText);
                    localStorage.removeItem("authToken");
                    localStorage.removeItem("userEmail");
                    alert("Access denied! You do not have permission.");
                }
            });
        }
    });
}


$('#register-btn').on('click', function(event) {
    event.preventDefault();

    let fullName = $('#fullName').val();
    let email = $('#emailR').val();
    let address = $('#address').val();
    let contact = $('#contactR').val();
    let password = $('#passwordR').val();
    let confirmPassword = $('#confirmPassword').val();

    if (!fullName || !email || !address || !contact || !password || !confirmPassword) {
        alert("Please fill in all fields.");
        return;
    }

    if (password !== confirmPassword) {
        alert("Passwords do not match!");
        return;
    }

    let newUser = {
        'name': fullName,
        'email': email,
        'address': address,
        'contact': contact,
        'password': password
    };

    $.ajax({
        url: `http://localhost:8080/api/v1/user/register`,
        method: `POST`,
        headers: {
            "Content-Type": "application/json"
        },
        data: JSON.stringify(newUser),
        success: function(response) {
            console.log("Registration Response:", response);
            alert("Registration successful!");
        },
        error: function(xhr) {
            console.log("Registration failed:", xhr.responseText);
            alert("Registration failed! Please try again.");
        }
    });
});