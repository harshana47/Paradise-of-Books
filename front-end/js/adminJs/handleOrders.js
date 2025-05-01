
const API_BASE_URL = "http://localhost:8080/api/v1/orders";

function fetchOrders() {
    const userRole = localStorage.getItem("userRole");

    if (userRole !== "ADMIN") {
        console.error("Unauthorized access!");
        return;
    }

    $.ajax({
        url: `${API_BASE_URL}/getAll`,
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("authToken")}`
        },
        success: function (orders) {
            loadOrders(orders);
        },
        error: function (xhr, status, error) {
            console.error("Error fetching orders:", error);
        }
    });
}

function loadOrders(orders) {
    let tableBody = document.getElementById("orderTable");
    tableBody.innerHTML = "";

    orders.forEach(order => {
        let row = `<tr>
                <td>${order.oid}</td>
                <td>$${order.totalPrice.toFixed(2)}</td>
                <td>${order.userId}</td>
                <td>${order.address}</td>
                <td>${order.contact}</td>
                <td>
                    <select onchange="updateStatus('${order.oid}', this)">
                        <option value="Complete" ${order.status === "Complete" ? "selected" : ""}>Complete</option>
                        <option value="Incomplete" ${order.status === "INCOMPLETE" ? "selected" : ""}>Incomplete</option>
                    </select>
                </td>
                <td><button onclick="saveStatus('${order.oid}')">Save</button></td>
            </tr>`;
        tableBody.innerHTML += row;
    });
}

function updateStatus(orderId, selectElement) {
    let order = { oid: orderId, status: selectElement.value };
    localStorage.setItem(`order-${orderId}`, JSON.stringify(order));
}

function saveStatus(orderId) {
    let orderData = JSON.parse(localStorage.getItem(`order-${orderId}`));
    if (!orderData) return alert("No changes detected");

    $.ajax({
        url: `${API_BASE_URL}/updateStatus/${orderId}`,
        method: 'PUT',
        contentType: 'application/json',
        headers: {
            'Authorization': `Bearer ${localStorage.getItem("authToken")}`
        },
        data: JSON.stringify({ status: orderData.status }),
        success: function () {
            alert(`Order ${orderId} status updated to: ${orderData.status}`);
            fetchOrders();
        },
        error: function () {
            alert("Failed to update order status");
        }
    });
}

window.onload = fetchOrders;
