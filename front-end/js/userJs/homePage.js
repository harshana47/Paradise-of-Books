document.getElementById("logOut").addEventListener("click", function () {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("userEmail")

    window.location.href = "../../index.html";
});
$(document).ready(function () {
    fetchCartCount();
    fetchBidCartCount();
    fetchBooks();
});

function fetchCartCount() {
    let userId = localStorage.getItem("userId");
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

let books = [];
let selectedBook = {};
let cartCount = 0;
let currentUserId = localStorage.getItem("userId") || 1;
let categories = [];

function fetchBooks() {
    $.ajax({
        url: 'http://localhost:8080/api/v1/books/ACTIVE',
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken')
        },

        success: function (data) {
            console.log('Fetched books:', data);
            books = data;
            fetchCategories();
            displayBooks();
        },
        error: function (xhr, status, error) {
            console.error('Error fetching books:', error);
        }
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

function fetchBooksByCategory(categoryId) {
    let filteredBooks = books.filter(book => book.categoryId === categoryId);
    displayBooks(filteredBooks);
}

function searchBooks() {
    const searchTerm = $('#searchInput').val().toLowerCase();
    let filteredBooks = books.filter(book => book.title.toLowerCase().includes(searchTerm));
    displayBooks(filteredBooks);
}

function displayBooks(filteredBooks = books) {
    let booksContainer = $('#booksContainer');
    booksContainer.empty();

    if (filteredBooks.length > 0) {
        let booksByCategory = {};

        filteredBooks.forEach(book => {
            if (!booksByCategory[book.categoryId]) {
                booksByCategory[book.categoryId] = {
                    categoryName: '',
                    books: []
                };
            }
            booksByCategory[book.categoryId].books.push(book);
        });

        Object.keys(booksByCategory).forEach(categoryId => {
            const category = booksByCategory[categoryId];

            getCategoryNameById(categoryId).then(categoryName => {
                category.categoryName = categoryName;

                booksContainer.append(`
                    <h3 class="mt-5" style="font-family: Garamond;font-size: 30px;text-transform: uppercase;font-weight: bold;margin-bottom: 40px;color: #0a53be">${"--"+category.categoryName+"--"}</h3>
                    <div class="row">
                `);

                category.books.forEach(book => {
                    const imageFilename = book.image ? book.image.split("\\").pop() : "default.jpg";
                    booksContainer.append(`
                        <div class="col-lg-3 col-md-4 col-sm-6 col-12">
                            <div class="card product-card" style="margin-bottom: 40px">
                                <img src="http://localhost:8080/api/v1/images/${imageFilename}" class="card-img-top" alt="${book.title}">
                                <div class="card-body text-center">
                                    <h5 class="card-title" style="margin-bottom: 20px">${book.title}</h5>
                                    <p class="card-text">$${book.price}</p>
                                    <p class="card-text">Only ${book.qty} left</p>
                                    <button class="btn btn-primary add-to-cart" data-id="${book.bid}">Add to Cart</button>
                                </div>
                            </div>
                        </div>
                    `);
                });

                booksContainer.append(`</div>`);
            });
        });
    } else {
        booksContainer.append('<p>No books found.</p>');
    }
}

// Handle adding books to cart
$(document).on('click', '.add-to-cart', function () {
    selectedBook = books.find(book => book.bid == $(this).data('id'));

    $('#modalTitle').text(selectedBook.title);
    $('#modalImage').attr('src', `http://localhost:8080/api/v1/images/${selectedBook.image.split("\\").pop()}`);
    $('#modalDescription').text(selectedBook.description || "No description available.");
    $('#modalPrice').text(`Price: $${selectedBook.price}`);
    $('#quantity').text(1);
    $('#cartModal').modal('show');
});

$('#increaseQty').click(function () {
    let qty = parseInt($('#quantity').text());
    $('#quantity').text(qty + 1);
});

$('#decreaseQty').click(function () {
    let qty = parseInt($('#quantity').text());
    if (qty > 1) {
        $('#quantity').text(qty - 1);
    }
});

$('#confirmAddToCart').click(function () {
    if (!localStorage.getItem('authToken')) {
        window.location.href = '../../index.html';
        return;
    }
    let quantity = parseInt($('#quantity').text());
    let totalPrice = selectedBook.price * quantity;

    $.ajax({
        url: 'http://localhost:8080/api/v1/bookCart/add',
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('authToken'),
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            bookId: selectedBook.bid,
            image: selectedBook.image,
            title: selectedBook.title,
            price: selectedBook.price,
            total: totalPrice,
            userId: currentUserId,
            qty: quantity
        }),
        success: function () {
            cartCount++;
            $('.cart-count').text(cartCount);
            $('#cartModal').modal('hide');
        },
        error: function (xhr, status, error) {
            console.error('Error adding to cart:', error);
        }
    });
});
function getCategoryNameById(categoryId) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: `http://localhost:8080/api/v1/category/${categoryId}`,
            method: 'GET',
            success: function (data) {
                if (data && data.name) {
                    resolve(data.name);
                } else {
                    resolve('Unknown Category');
                }
            },
            error: function (xhr, status, error) {
                console.error('Error fetching category by id:', error);
                resolve('Unknown Category');
            }
        });
    });
}

var map = L.map('map').setView([7.8731, 80.7718], 7);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

function searchLibraries(query) {
    var url = 'https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(query) + '&countrycodes=LK&limit=1000';
    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data && data.length > 0) {
                data.forEach(function(place) {
                    var lat = place.lat;
                    var lon = place.lon;
                    var title = place.display_name;

                    console.log('Library found: ', title, lat, lon);

                    L.marker([lat, lon]).addTo(map)
                        .bindPopup(title)
                        .openPopup();
                });
            } else {
                console.log('No libraries found!');
            }
        })
        .catch(error => console.error('Error searching libraries:', error));
}

var queries = ['library', 'book store', 'reading room' ,'libraries'];
queries.forEach(query => searchLibraries(query));
