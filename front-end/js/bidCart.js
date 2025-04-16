
let userId = localStorage.getItem("userId");
let token = localStorage.getItem("authToken");

if (!userId || !token) {
    console.error("Authentication data missing. Redirecting...");
    window.location.href = "../index.html";
}

function showCheckoutModal() {
    const modal = new bootstrap.Modal(document.getElementById('checkoutModal'));
    modal.show();

    document.getElementById("customAddress").addEventListener("change", () => {
        document.getElementById("customAddressForm").style.display = "block";
    });

    document.getElementById("defaultAddress").addEventListener("change", () => {
        document.getElementById("customAddressForm").style.display = "none";
    });
}

// // Checkout button action
// document.getElementById("checkoutBtn").addEventListener("click", async () => {
//     const paymentMethod = document.getElementById("paymentMethod").value;
//
//     if (paymentMethod === "pay_now") {
//         await placeOrder();
//     } else if (paymentMethod === "cash_on_delivery") {
//         await addEventListener();
//     }
// });

//payment via PayHere
async function placeOrder() {
    const addressOption = document.querySelector('input[name="addressOption"]:checked')?.id;
    let address = "", contact = "", totally = 0;

    if (!addressOption) {
        alert("Please select an address option.");
        return;
    }

    if (addressOption === 'customAddress') {
        address = document.getElementById('address').value;
        contact = document.getElementById('contact').value;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/v1/bidCart/user/${userId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        if (!response.ok) throw new Error("Failed to fetch cart data");
        const data = await response.json();

        if (!data || data.length === 0) {
            alert("Your cart is empty.");
            return;
        }

        totally = data.reduce((sum, item) => sum + item.maxPrice, 0);
        const orderDetails = data.map(item => ({
            price: item.maxPrice,
            title: item.title
        }));

        const paymentRequest = {
            userId,
            totalPrice: totally,
            orderDetailsList: orderDetails,
            address: addressOption === 'defaultAddress' ? null : address,
            contact: addressOption === 'defaultAddress' ? null : contact
        };
        console.log("Order data saved to localStorage:", paymentRequest);
        localStorage.setItem("pendingOrder", JSON.stringify(paymentRequest));
        const tempOrderId = `OID-${Date.now()}`;
        const name = localStorage.getItem('name') || 'Customer';
        const email = localStorage.getItem('userEmail') || 'example@mail.com';
        const currency = "LKR";
        const amountFormatted = parseFloat(totally).toFixed(2);
        const merchantId = "1229934";
        const merchantSecret = "MjE1MTA0MzQ5NjE5MTIyMzU4MTAzMDk0NzM4NDY0MjE0MzAyNTE=";

        const hashedSecret = CryptoJS.MD5(merchantSecret).toString(CryptoJS.enc.Hex).toUpperCase();
        const hash = CryptoJS.MD5(merchantId + tempOrderId + amountFormatted + currency + hashedSecret).toString(CryptoJS.enc.Hex).toUpperCase();

        const postData = {
            merchant_id: merchantId,
            return_url: "http://localhost:8080/api/v1/payments/successY",
            cancel_url: "http://localhost:8080/api/v1/payment-cancel",
            notify_url: "http://localhost:8080/api/v1/payments/notify",
            first_name: name,
            last_name: name,
            email,
            phone: contact,
            address,
            city: "Colombo",
            country: "Sri Lanka",
            order_id: tempOrderId,
            items: "Book Order",
            currency,
            amount: amountFormatted,
            hash
        };

        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'https://sandbox.payhere.lk/pay/checkout';

        for (const key in postData) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = postData[key];
            form.appendChild(input);
        }

        document.body.appendChild(form);
        form.submit();

    } catch (error) {
        console.error("Checkout failed:", error);
        alert("Failed to proceed with payment.");
    }
}

function loadCart() {
    fetch(`http://localhost:8080/api/v1/bidCart/user/${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    })
        .then(res => res.json())
        .then(data => {
            let cartHTML = "", total = 0;
            data.forEach(item => {
                total += item.maxPrice;
                const imageFilename = item.image ? item.image.split("\\").pop() : "default.jpg";
                cartHTML += `
                        <div class="cart-item" id="item-${item.bcid}">
                            <img src="http://localhost:8080/api/v1/images/${imageFilename}" alt="${item.title}">
                            <span>${item.title} - $${item.maxPrice}</span>
                            <button class="btn btn-remove" onclick="removeItem('${item.bcid}')">Remove</button>
                        </div>`;
            });

            document.getElementById("cartItems").innerHTML = cartHTML;
            document.getElementById("totalPrice").innerText = total.toFixed(2);
        })
        .catch(error => console.error("Error loading cart:", error));
}

// Remove item from cart
function removeItem(bcid) {
    fetch(`http://localhost:8080/api/v1/bidCart/delete/${bcid}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    }).then(() => {
        document.getElementById(`item-${bcid}`).style.transform = "scale(0)";
        setTimeout(loadCart, 300);
    });
}

function clearCart() {
    fetch(`http://localhost:8080/api/v1/bidCart/deleteAll/${userId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    }).then(() => {
        document.getElementById("cartItems").innerHTML = "";
        document.getElementById("totalPrice").innerText = "0.00";
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('order_id');
    const token = localStorage.getItem('authToken');
    const orderDataStr = localStorage.getItem('pendingOrder');
    if (!orderDataStr) return;

    const orderData = JSON.parse(orderDataStr);
    orderData.status = "paid";

    fetch(`http://localhost:8080/api/v1/orders/place`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(orderData)
    })
        .then(res => {
            if (!res.ok) throw new Error("Order save failed");
            return res.json();
        })
        .then(() => {
            alert("Order successfully placed!");
            localStorage.removeItem("pendingOrder");

            return fetch(`http://localhost:8080/api/v1/bookCart/deleteAll/${userId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            });
        })
        .then(() => window.location.href = "bookCart.html")
        .catch(err => {
            console.error("Error finalizing order:", err);
            alert("Error finalizing your order.");
        });
});

function continueShopping() {
    window.location.href = "homePage.html";
}

document.addEventListener("DOMContentLoaded", loadCart);