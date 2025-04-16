
$(document).ready(function () {
    const userRole = localStorage.getItem("userRole");

    if (userRole !== "ADMIN") {
        console.error("Unauthorized access!");
        return;
    }
    fetchUsers();
});

function fetchUsers() {
    $.ajax({
        url: 'http://localhost:8080/api/v1/user/getAll',
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function (data) {
            console.log("API Response:", data);
            let users = data.users || data;

            if (!Array.isArray(users)) {
                console.error("Expected an array but got:", typeof users);
                return;
            }

            let tbody = $('#userTableBody');
            tbody.empty();
            users.forEach((user, index) => {
                tbody.append(`
            <tr>
                <td>${index + 1}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>
                    <select class="form-select" onchange="changeRole('${user.uid}', this.value)">
                        <option value="USER" ${user.role === 'USER' ? 'selected' : ''}>USER</option>
                        <option value="ADMIN" ${user.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                    </select>
                </td>

            </tr>
        `);
            });
        },
        error: function (xhr, status, error) {
            console.error('Error fetching users:', error);
        }
    });
}
console.log(localStorage.getItem('authToken'));

function changeRole(userId, newRole) {
    $.ajax({
        url: `http://localhost:8080/api/v1/user/${userId}/role`,
        method: 'PUT',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken'),
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({ role: newRole }),
        success: function () {
            fetchUsers();
        },
        error: function (xhr, status, error) {
            console.error('Error changing role:', error);
        }
    });
}

