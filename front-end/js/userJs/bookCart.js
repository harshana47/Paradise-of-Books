let userId = localStorage.getItem("userId");
console.log(userId);
if (!userId) {
    console.error("User ID not found. Redirecting to login...");
    window.location.href = "../../index.html";
}

let token = localStorage.getItem("authToken");
if (!token) {
    console.error("Token not found. Redirecting to login...");
    window.location.href = "../../index.html";
}

function showCheckoutModal() {
    const modal = new bootstrap.Modal(document.getElementById('checkoutModal'));
    modal.show();

    document.getElementById("customAddress").addEventListener("change", function () {
        document.getElementById("customAddressForm").style.display = "block";
    });

    document.getElementById("defaultAddress").addEventListener("change", function () {
        document.getElementById("customAddressForm").style.display = "none";
    });
}
let contact ="";
let namee ="";
function loadCart() {
    fetch(`http://localhost:8080/api/v1/bookCart/user/${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    })
        .then(response => response.json())
        .then(data => {
            contact = data.contact;
            namee = data.name;
            let cartHTML = "";
            let total = 0;
            data.forEach(item => {
                total += item.price;

                const imageFilename = item.image ? item.image.split("\\").pop() : "default.jpg";
                cartHTML += `
                <div class="cart-item" id="item-${item.bkcid}">
                    <img src="http://localhost:8080/api/v1/images/${imageFilename}" alt="${item.title}">
                    <span>${item.title} - $${item.price}</span>
                    <button class="btn btn-remove" onclick="removeItem('${item.bkcid}')">Remove</button>
                </div>`;
            });
            document.getElementById("cartItems").innerHTML = cartHTML;
            document.getElementById("totalPrice").innerText = total.toFixed(2);
        })
        .catch(error => console.error("Error loading cart:", error));
}

function removeItem(bkcid) {
    fetch(`http://localhost:8080/api/v1/bookCart/delete/${bkcid}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
    })
        .then(() => {
            document.getElementById(`item-${bkcid}`).style.transform = "scale(0)";
            setTimeout(loadCart, 300);
        });
}

function clearCart() {
    fetch(`http://localhost:8080/api/v1/bookCart/deleteAll/${userId}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
    })
        .then(() => {
            document.getElementById("cartItems").innerHTML = "";
            document.getElementById("totalPrice").innerText = "0.00";
        });
}

function placeOrder() {
    const addressOption = document.querySelector('input[name="addressOption"]:checked').id;
    let address = "";
    let contact = "";
    let totally = 0;

    if (addressOption === 'customAddress') {
        address = document.getElementById('address').value;
        contact = document.getElementById('contact').value;
    }

    fetch(`http://localhost:8080/api/v1/bookCart/user/${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch cart data");
            return response.json();
        })
        .then(data => {
            if (!data || data.length === 0) {
                throw new Error("Cart is empty. Cannot proceed with checkout.");
            }

            totally = data.reduce((sum, item) => sum + item.price, 0);
            const orderDetails = data.map(item => ({
                price: item.price,
                title: item.title
            }));

            const tempOrder = {
                userId,
                totalPrice: totally,
                orderDetailsList: orderDetails,
                address: addressOption === 'defaultAddress' ? null : address,
                contact: addressOption === 'defaultAddress' ? null : contact
            };
            console.log("Order data saved to localStorage:", tempOrder);
            localStorage.setItem("pendingOrder", JSON.stringify(tempOrder));

            const tempOrderId = `OID-${Date.now()}`;

            const merchantId = "1229934";
            const merchantSecret = "MjE1MTA0MzQ5NjE5MTIyMzU4MTAzMDk0NzM4NDY0MjE0MzAyNTE=";
            const namee = localStorage.getItem('name');
            const email = localStorage.getItem('email');
            const currency = "LKR";
            const amountFormatted = parseFloat(totally).toFixed(2).replaceAll(',', '');
            const hashedSecret = CryptoJS.MD5(merchantSecret).toString(CryptoJS.enc.Hex).toUpperCase();
            const hash = CryptoJS.MD5(merchantId + tempOrderId + amountFormatted + currency + hashedSecret).toString(CryptoJS.enc.Hex).toUpperCase();
            const returnUrl = "http://localhost:8080/api/v1/payments/success";

            const postData = {
                merchant_id: merchantId,
                return_url: returnUrl,
                cancel_url: "http://localhost:8080/api/v1/payment-cancel",
                notify_url: " http://localhost:8080/api/v1/payments/notify",
                first_name: namee,
                last_name: namee,
                email: email,
                phone: contact,
                address: address,
                city: "Colombo",
                country: "Sri Lanka",
                order_id: tempOrderId,
                items: "Book Order",
                currency: currency,
                amount: parseFloat(totally).toFixed(2),
                hash: hash
            };

            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'https://sandbox.payhere.lk/pay/checkout';

            for (const key in postData) {
                if (postData.hasOwnProperty(key)) {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = key;
                    input.value = postData[key];
                    form.appendChild(input);
                }
            }

            document.body.appendChild(form);
            form.submit();
        })
        .catch(error => {
            console.error("Checkout failed:", error);
            alert("Failed to proceed to payment. Please try again.");
        });
}
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('order_id');
    const token = localStorage.getItem('authToken');
    console.log(localStorage.getItem("pendingOrder"));
    const orderData = JSON.parse(localStorage.getItem('pendingOrder'));

    orderData.status = "paid";

    fetch(`http://localhost:8080/api/v1/orders/place`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(orderData)
    })
        .then(response => {
            if (!response.ok) throw new Error("Order save failed");
            return response.json();
        })
        .then(savedOrder => {
            alert("Your order was successfully placed!");
            console.log("Order save response:", savedOrder);
            localStorage.removeItem("pendingOrder");

            fetch(`http://localhost:8080/api/v1/bookCart/deleteAll/${orderData.userId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            });

            window.location.href = "bookCart.html";
        })
        .catch(err => {
            console.error("Error confirming order:", err);
            alert("Something went wrong while confirming your order.");
        });
});

function continueShopping() {
    window.location.href = "homePage.html";
}

document.addEventListener("DOMContentLoaded", loadCart);