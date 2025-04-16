document.addEventListener("DOMContentLoaded", () => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        alert("User not logged in!");
        return;
    }

    fetch(`http://localhost:8080/api/v1/books/sales?userId=${userId}`)
        .then(response => response.json())
        .then(data => displaySales(data))
        .catch(error => console.error("Error fetching book sales:", error));
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
        const statusClass = sale.bookStatus.toLowerCase();

        card.innerHTML = `
                <img src="http://localhost:8080/api/v1/images/${imageFilename}" alt="${sale.title}">
                <h3>${sale.title}</h3>
                <p><strong>Author:</strong> ${sale.author}</p>
                <p><strong>Price:</strong> $${sale.price}</p>
                <p><strong>Quantity:</strong> ${sale.qty}</p>
                <p><strong>Year:</strong> ${sale.publishedYear}</p>
                <p><strong>Description:</strong> ${sale.description}</p>
                <p><strong>Status:</strong> <span class="status ${statusClass}">${sale.bookStatus}</span></p>
                <button class="delete-btn" onclick="deleteSale('${sale.bid}')">Delete</button>
            `;

        container.appendChild(card);
    });
}

function deleteSale(bookId) {
    if (!confirm("Are you sure you want to delete this book?")) return;

    fetch(`http://localhost:8080/api/v1/books/delete/${bookId}`, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${localStorage.getItem("authToken")}` }
    })
        .then(() => {
            alert("Book deleted successfully!");
            location.reload();
        })
        .catch(error => console.error("Error deleting book:", error));
}
