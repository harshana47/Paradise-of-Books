
const API_BASE_URL = "http://localhost:8080/api/v1/orders";

async function fetchOrders() {
    const userRole = localStorage.getItem("userRole");

    if (userRole !== "ADMIN") {
        console.error("Unauthorized access!");
        return;
    }
    try {
        let response = await fetch(`${API_BASE_URL}/getAll`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem("authToken")}` }
        });
        let orders = await response.json();
        loadOrders(orders);
    } catch (error) {
        console.error("Error fetching orders:", error);
    }
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

async function saveStatus(orderId) {
    let orderData = JSON.parse(localStorage.getItem(`order-${orderId}`));
    if (!orderData) return alert("No changes detected");

    try {
        let response = await fetch(`${API_BASE_URL}/updateStatus/${orderId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem("authToken")}`
            },
            body: JSON.stringify({ status: orderData.status })
        });

        if (response.ok) {
            alert(`Order ${orderId} status updated to: ${orderData.status}`);
            fetchOrders();
        } else {
            alert("Failed to update order status");
        }
    } catch (error) {
        console.error("Error updating order:", error);
    }
}

window.onload = fetchOrders;
