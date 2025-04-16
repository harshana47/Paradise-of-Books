window.onload = function() {
    fetchActiveBids();
    fetchCategories();
    fetchCartCount();
    fetchBidCartCount();
};

document.getElementById("logOut").addEventListener("click", function () {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("userEmail")

    window.location.href = "../../index.html";
});

function fetchCartCount() {
    let userId = localStorage.getItem("userId"); // Get logged-in user ID
    if (!userId) {
        console.warn("No user ID found in localStorage.");
        return;
    }

    $.ajax({
        url: `http://localhost:8080/api/v1/bookCart/count/${userId}`,
        method: "GET",
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function (response) {
            console.log("Cart count:", response);
            $(".cart-count").text(response.count);
        },
        error: function (xhr, status, error) {
            console.error("Error fetching cart count:", error);
        }
    });
}

function fetchBidCartCount() {
    let userId = localStorage.getItem("userId");
    if (!userId) {
        console.warn("No user ID found in localStorage.");
        return;
    }

    $.ajax({
        url: `http://localhost:8080/api/v1/bidCart/count/${userId}`,
        method: "GET",
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },
        success: function (response) {
            console.log("Bid cart count:", response);
            $(".bidCart-count").text(response.count);
        },
        error: function (xhr, status, error) {
            console.error("Error fetching bid cart count:", error);
        }
    });
}

function fetchActiveBids() {
    fetch('http://localhost:8080/api/v1/bidding/active', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        }
    })
        .then(response => response.json())
        .then(data => {
            displayBiddingCards(data);
        })
        .catch(error => console.error('Error fetching active bids:', error));
}

function displayBiddingCards(bids) {
    const biddingCards = document.getElementById('biddingCards');
    biddingCards.innerHTML = '';

    bids.forEach(bid => {
        const imageFilename = bid.image ? bid.image.split("\\").pop() : "default.jpg";
        const imageUrl = `http://localhost:8080/api/v1/images/${imageFilename}`;

        fetch(`http://localhost:8080/api/v1/bidStorage/maxBid/${bid.bidId}`)
            .then(response => {
                if (!response.ok) {
                    console.warn(`No max bid found for bidId: ${bid.bidId}, using default bidAmount: ${bid.bidAmount}`);
                    return { maxPrice: bid.bidAmount };
                }
                return response.json();
            })
            .then(maxBidData => {
                console.log(`Max bid response for ${bid.bidId}:`, maxBidData);
                const maxPrice = (maxBidData && maxBidData > bid.bidAmount)
                    ? maxBidData
                    : bid.bidAmount;

                const card = document.createElement('div');
                card.classList.add('card');
                card.innerHTML = `
            <img src="${imageUrl}" alt="${bid.title}">
            <div class="card-content">
                <h2>${bid.title}</h2>
                <p>Max Bid: $${maxPrice}</p>
                <button class="bid-button" onclick="openBidModal('${bid.bidId}', '${bid.title}', '${bid.author}', '${bid.description}', '${imageUrl}', ${maxPrice})">Bid</button>
            </div>
        `;
                biddingCards.appendChild(card);
            })
            .catch(error => {
                console.error('Error fetching max bid:', error);
            });

    });
}

function openBidModal(bidId, title, author, description, image, maxBid) {
    document.getElementById('modalImage').src = image;
    document.getElementById('modalTitle').textContent = title;
    document.getElementById('modalAuthor').textContent = author;
    document.getElementById('modalMaxBid').textContent = maxBid;
    document.getElementById('modalDescription').textContent = description;
    document.getElementById('bidModal').style.display = 'flex';

    document.getElementById('placeBidBtn').onclick = function() {
        const bidAmount = parseFloat(document.getElementById('bidAmount').value);

        if (!bidAmount || isNaN(bidAmount)) {
            alert('Please enter a valid bid amount.');
            return;
        }

        if (bidAmount <= maxBid) {
            alert(`Your bid must be higher than the current max bid ($${maxBid}).`);
            return;
        }

        placeBid(bidId, bidAmount);
    };
}

function placeBid(bidId, bidAmount) {
    const token = localStorage.getItem('authToken');
    if (!token) {
        alert('You need to log in first.');
        return;
    }

    if (!bidId) {
        console.error("Bid ID is missing");
        alert('Bid ID cannot be null.');
        return;
    }

    const userId = localStorage.getItem('userId');
    if (!userId) {
        console.error("User ID is missing");
        alert('User ID cannot be null.');
        return;
    }

    const payload = {
        bidId: bidId,
        bidAmount: bidAmount,
        userId: userId
    };

    console.log("Request Payload:", JSON.stringify(payload));

    fetch('http://localhost:8080/api/v1/bidStorage/placeBid', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(data.message || 'Unknown error occurred while placing bid');
                });
            }
            return response.json();
        })
        .then(() => {
            alert('Bid placed successfully!');
            document.getElementById('bidModal').style.display = 'none';
            fetchActiveBids();
        })
        .catch(error => {
            alert('Error placing bid: ' + error.message);
            console.error('Error placing bid:', error);
        });
}
function fetchCategories() {
    $.ajax({
        url: 'http://localhost:8080/api/v1/category/list',
        method: 'GET',
        success: function (response) {
            console.log('Fetched categories:', response);
            if (response.data && Array.isArray(response.data)) {
                categories = response.data;
                populateCategoryDropdown(categories);
            } else {
                console.error("Categories are not in the expected format:", response);
            }
        },
        error: function (xhr, status, error) {
            console.error('Error fetching categories:', error);
        }
    });
}

function populateCategoryDropdown(categories) {
    if (Array.isArray(categories)) {
        const categoryDropdown = $('#categoryDropdown');
        categoryDropdown.empty();

        categories.forEach(category => {
            categoryDropdown.append(`
                <li><a class="dropdown-item" href="#" data-category="${category.id}" onclick="fetchBooksByCategory(${category.id})">${category.name}</a></li>
            `);
        });
    } else {
        console.error("Expected categories to be an array:", categories);
    }
}