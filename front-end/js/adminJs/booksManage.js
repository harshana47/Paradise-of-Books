
document.addEventListener("DOMContentLoaded", () => {
    const userRole = localStorage.getItem("userRole");

    if (userRole !== "ADMIN") {
        console.error("Unauthorized access!");
        return;
    }
    fetch("http://localhost:8080/api/v1/books/byStatus?status=DEACTIVATED")
        .then(response => response.json())
        .then(data => displaySales(data))
        .catch(error => console.error("Error fetching books:", error));
});

function displaySales(books) {
    const container = document.getElementById("salesContainer");
    container.innerHTML = "";

    books.forEach(book => {
        console.log("Book data:", book);

        if (!book.bid) {
            console.error("Error: book.bid is missing!");
            return;
        }

        const card = document.createElement("div");
        card.classList.add("card");

        const imageFilename = book.image ? book.image.split("\\").pop() : "default.jpg";

        card.innerHTML = `
            <img src="http://localhost:8080/api/v1/images/${imageFilename}" alt="${book.title}">
            <div class="info">
                <h3>${book.title}</h3>
                <p><strong>Author:</strong> ${book.author}</p>
                <p><strong>Price:</strong> $${book.price}</p>
                <p><strong>Year:</strong> ${book.publishedYear}</p>
                <p><strong>Qty:</strong> ${book.qty}</p>
                <p>${book.description}</p>
            </div>
            <div class="buttons">
                <button class="accept" onclick="changeStatus('${book.bid}', 'ACTIVE')">Accept</button>
                <button class="decline" onclick="changeStatus('${book.bid}', 'DELETE')">Decline (Delete)</button>
            </div>
        `;

        container.appendChild(card);
    });
}

function changeStatus(bookId, status) {
    if (status === "DELETE") {
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
    } else {
        if (!bookId) {
            console.error("Error: bookId is undefined!");
            return;
        }
        fetch(`http://localhost:8080/api/v1/books/changeStatus/${bookId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${localStorage.getItem("authToken")}`
            },
            body: JSON.stringify({ activeStatus: status })
        })
            .then(response => {
                if (!response.ok) throw new Error("Failed to update status");
                return response.text().then(text => text ? JSON.parse(text) : {});
            })
            .then(() => {
                alert(`Book status changed to ${status}`);
                location.reload();
            })
            .catch(error => console.error("Error updating book:", error));
    }
}
