
document.addEventListener("DOMContentLoaded", () => {
    const userRole = localStorage.getItem("userRole");

    if (userRole !== "ADMIN") {
        console.error("Unauthorized access!");
        return;
    }
    fetch("http://localhost:8080/api/v1/bidding/byStatus?status=closed")  // Adjust API URL
        .then(response => response.json())
        .then(data => displaySales(data))
        .catch(error => console.error("Error fetching bids:", error));
});

function displaySales(bids) {
    const container = document.getElementById("bidsContainer");
    container.innerHTML = "";

    bids.forEach(bid => {
        console.log("Book data:", bids);  // 🔹 Log book object

        if (!bid.bidId) {
            console.error("Error: book.bid is missing!");
            return;
        }

        const card = document.createElement("div");
        card.classList.add("card");

        const imageFilename = bid.image ? bid.image.split("\\").pop() : "default.jpg";

        card.innerHTML = `
            <img src="http://localhost:8080/api/v1/images/${imageFilename}" alt="${bid.title}">
            <div class="info">
                <h3>${bid.title}</h3>
                <p><strong>Author:</strong> ${bid.author}</p>
                <p><strong>Price:</strong> $${bid.bidAmount}</p>
                <p><strong>Description:</strong> $${bid.description}</p>
                <p><strong>Date:</strong> ${bid.bidDate}</p>
            </div>
            <div class="buttons">
                <button class="accept" onclick="changeStatus('${bid.bidId}', 'active')">Accept</button>
                <button class="decline" onclick="changeStatus('${bid.bidId}', 'DELETE')">Decline (Delete)</button>
            </div>
        `;

        container.appendChild(card);
    });
}

function changeStatus(bidId, status) {
    if (status === "DELETE") {
        if (!confirm("Are you sure you want to delete this book?")) return;

        fetch(`http://localhost:8080/api/v1/bidding/delete/${bidId}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${localStorage.getItem("authToken")}` }
        })
            .then(() => {
                alert("Book deleted successfully!");
                location.reload();
            })
            .catch(error => console.error("Error deleting bid:", error));
    } else {
        if (!bidId) {
            console.error("Error: bookId is undefined!");
            return;
        }
        fetch(`http://localhost:8080/api/v1/bidding/changeStatus/${bidId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("authToken")}`
            },
            body: JSON.stringify({ status: status })
        })
            .then(response => {
                if (!response.ok) throw new Error("Failed to update status");
                return response.text().then(text => text ? JSON.parse(text) : {});
            })
            .then(() => {
                alert(`Bid status changed to ${status}`);
                location.reload();
            })
            .catch(error => console.error("Error updating bid:", error));
    }
}
