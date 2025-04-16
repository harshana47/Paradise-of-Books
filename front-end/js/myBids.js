document.addEventListener("DOMContentLoaded", () => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        alert("User not logged in!");
        return;
    }

    fetch(`http://localhost:8080/api/v1/bidding/byUser?userId=${userId}`)
        .then(response => response.json())
        .then(data => {
            console.log("API Response:", data);

            if (Array.isArray(data)) {
                displaySales(data);
            } else {
                console.error("Error: Response data is not an array.", data);
                alert("Something went wrong, please try again.");
            }
        })
        .catch(error => {
            console.error("Error fetching sales:", error);
            alert("Failed to fetch sales data.");
        });
});

function displaySales(sales) {
    const container = document.getElementById("salesContainer");
    container.innerHTML = "";

    if (sales.length === 0) {
        container.innerHTML = "<p>No ongoing sales found.</p>";
        return;
    }

    sales.forEach(sale => {
        const card = document.createElement("div");
        card.classList.add("card");

        const imageFilename = sale.image ? sale.image.split("\\").pop() : "default.jpg";

        fetchBids(sale.bidId).then(bids => {
            let bidPrices = '';
            if (Array.isArray(bids)) {
                bids.forEach(bid => {
                    bidPrices += `<p>Max Price: $${bid.maxPrice}</p>`;
                });
            }

            card.innerHTML = `
                <img src="http://localhost:8080/api/v1/images/${imageFilename}" alt="${sale.title}">
                <h3>${sale.title}</h3>
                <p><strong>Author:</strong> ${sale.author}</p>
                <p><strong>Price:</strong> $${sale.bidAmount}</p>
                <p><strong>Date:</strong> ${sale.bidDate}</p>
                <p><strong>Descreption:</strong>${sale.description}</p>
                <div class="bid-list">${bidPrices}</div>
                <button class="delete-btn" onclick="deleteSale('${sale.bidId}')">Delete</button>
                <button class="end-btn" onclick="endBid('${sale.bidId}')">End Bid</button>
            `;
            container.appendChild(card);
        });
    });
}

function fetchBids(biddingId) {
    return fetch(`http://localhost:8080/api/v1/bidding/bids/${biddingId}`)
        .then(response => response.json())
        .catch(error => console.error("Error fetching bids:", error));
}

function deleteSale(bidId) {
    if (!confirm("Are you sure you want to delete this bid?")) return;

    fetch(`http://localhost:8080/api/v1/bidding/deleteStorage/${bidId}`, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${localStorage.getItem("authToken")}` }
    })
        .then(() => {
            alert("Bid deleted successfully!");
            location.reload();
        })
        .catch(error => console.error("Error deleting bid:", error));
}

function endBid(bidId) {
    if (!confirm("Are you sure you want to end this bid and move it to the cart?")) return;

    fetch(`http://localhost:8080/api/v1/bidding/end/${bidId}`, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${localStorage.getItem("authToken")}`,
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.ok) {
                alert("Bid moved to cart and closed successfully!");
                location.reload();
            } else {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || 'Failed to end the bid');
                });
            }
        })
        .catch(error => {
            console.error("Error ending bid:", error);
            alert("An error occurred: " + error.message);
        });
}


