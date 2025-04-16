
const userId = localStorage.getItem('userId');
const token = localStorage.getItem('authToken');
console.log(localStorage.getItem("userEmail"))

if (userId) {
    $('#userId').val(userId);
} else {
    $('#userId').val('Unknown User');
}

$(document).ready(function () {
    $('#bidCheckbox').on('change', function () {
        if ($(this).is(':checked')) {
            $('#saleCheckbox').prop('checked', false);
            $('#bidSection').show();
            $('#saleSection').hide();
        } else {
            $('#bidSection').hide();
        }
    });

    function fetchCategories() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/category/list',
            method: 'GET',
            headers: { "Content-Type": "application/json" },
            success: function (data) {
                const categories = data.data;
                let categoryOptions = '';
                categories.forEach(function (category) {
                    categoryOptions += `<option value="${category.cid}">${category.name}</option>`;
                });
                $('#category').html(categoryOptions);
                $('#categorySale').html(categoryOptions);
            },
            error: function (err) {
                console.error('Error fetching categories:', err);
                Swal.fire("Error", "Failed to load categories!", "error");
            }
        });
    }

    fetchCategories();

    $('#saleCheckbox').on('change', function () {
        if ($(this).is(':checked')) {
            $('#bidCheckbox').prop('checked', false);
            $('#saleSection').show();
            $('#bidSection').hide();
        } else {
            $('#saleSection').hide();
        }
    });

    $('#bidForm').on('submit', function (e) {
        e.preventDefault();

        const description = $('#description').val();
        const descriptionPattern = /^[A-Za-z0-9\s.,!?]+$/;

        if (!descriptionPattern.test(description)) {
            Swal.fire("Validation Error", "Description contains invalid characters!", "warning");
            return;
        }

        let bidData = new FormData();
        bidData.append('userId', $('#userId').val());
        bidData.append('categoryId', $('#category').val());
        bidData.append('bidAmount', $('#bidPrice').val());
        bidData.append('description', description);
        bidData.append('author', $('#author').val());
        bidData.append('title', $('#title').val());
        bidData.append('bidDate', new Date().toISOString().slice(0, 19));
        bidData.append('status', 'closed');
        bidData.append('email', localStorage.getItem("userEmail"))

        if ($('#imageBid')[0].files[0]) {
            bidData.append('image', $('#imageBid')[0].files[0]);
        }

        $.ajax({
            url: 'http://localhost:8080/api/v1/bidding/place',
            type: 'POST',
            headers: { "Authorization": "Bearer " + token },
            data: bidData,
            processData: false,
            contentType: false,
            success: function(response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Bid Submitted Successfully!',
                    text: "Your bid is under review. We'll review it within 24 hours, and you can check its status on the 'Pending Bids' page.",
                    confirmButtonText: 'Got it!',
                    confirmButtonColor: '#3085d6'
                });
                $('#bidForm')[0].reset();
            }
            ,
            error: function (err) {
                Swal.fire("Error", "Failed to place bid!", "error");
            }
        });
    });

    $('#saleForm').on('submit', function (e) {
        e.preventDefault();

        const descriptionSale = $('#descriptionSale').val();
        const descriptionSalePattern = /^[A-Za-z0-9\s.,!?]+$/;

        if (!descriptionSalePattern.test(descriptionSale)) {
            Swal.fire("Validation Error", "Description contains invalid characters!", "warning");
            return;
        }

        let formData = new FormData();
        formData.append('userId', $('#userId').val());
        formData.append('categoryId', $('#categorySale').val());
        formData.append('bookStatus', $('#bookStatusSale').val());
        formData.append('author', $('#authorSale').val());
        formData.append('title', $('#titleSale').val());
        formData.append('price', $('#salePrice').val());
        formData.append('description', descriptionSale);
        formData.append('qty', $('#qtySale').val());
        formData.append('publishedYear', $('#publishedYearSale').val());
        formData.append('activeStatus', 'DEACTIVATED');
        formData.append('email', localStorage.getItem("userEmail"))

        if ($('#imageSale')[0].files[0]) {
            formData.append('image', $('#imageSale')[0].files[0]);
        }

        $.ajax({
            url: 'http://localhost:8080/api/v1/books/place',
            type: 'POST',
            headers: { "Authorization": "Bearer " + token },
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                Swal.fire({
                    icon: 'success',
                    title: 'Bid Submitted Successfully!',
                    text: "Your book is under review. We'll review it within 24 hours, and you can check its status on the 'Pending Bids' page.",
                    confirmButtonText: 'Got it!',
                    confirmButtonColor: '#3085d6'
                });
                $('#saleForm')[0].reset();
            },
            error: function (err) {
                Swal.fire("Error", "Failed to list item for sale!", "error");
            }
        });
    });
});
