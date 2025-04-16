document.getElementById('changePasswordBtn').addEventListener('click', function() {
    window.location.href = 'forgotPassword.html';
});
const profileForm = document.getElementById('profileForm');
const alertMessage = document.getElementById('alertMessage');

profileForm.addEventListener('submit', function(event) {
    event.preventDefault();

    const user = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        contact: document.getElementById('contact').value,
        address: document.getElementById('address').value,
    };

    updateProfile(user);
});

function updateProfile(user) {
    const userId = localStorage.getItem("userId");

    // Show loading alert
    alertMessage.style.display = 'block';
    alertMessage.textContent = 'Updating profile...';

    fetch(`http://localhost:8080/api/v1/user/update/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem("authToken")}`
        },
        body: JSON.stringify(user)
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alertMessage.textContent = 'Profile updated successfully!';
                alertMessage.classList.add('success');
                alertMessage.style.color = '#5bc0de';
            } else {
                alertMessage.textContent = 'Error updating profile. Please try again.';
                alertMessage.classList.remove('success');
                alertMessage.style.color = '#d9534f';
            }
        })
        .catch(error => {
            alertMessage.textContent = 'There was an error. Please try again later.';
            alertMessage.classList.remove('success');
            alertMessage.style.color = '#d9534f';
        });
}

window.addEventListener('DOMContentLoaded', function() {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        window.location.href = "../index.html";
    }

    fetch(`http://localhost:8080/api/v1/user/getById/${userId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("authToken")}`
        }
    })
        .then(response => response.json())
        .then(userData => {
            document.getElementById('name').value = userData.name;
            document.getElementById('email').value = userData.email;
            document.getElementById('contact').value = userData.contact;
            document.getElementById('address').value = userData.address;
        })
        .catch(error => console.error('Error fetching user data:', error));
});