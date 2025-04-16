
// Save Category with AJAX
$('#categoryForm').submit(function(e) {
    e.preventDefault();
    const userRole = localStorage.getItem("userRole");

    if (userRole !== "ADMIN") {
        console.error("Unauthorized access!");
        return;
    }
    var categoryName = $('#categoryName').val();
    $.ajax({
        url: 'http://localhost:8080/api/v1/category/save',
        method: 'POST',
        data: JSON.stringify({ name: categoryName }),
        contentType: 'application/json',
        success: function(response) {
            $('#success-alert').fadeIn();
            $('#error-alert').hide();
            $('#categoryName').val('');
            loadCategories();
        },
        error: function(xhr, status, error) {
            $('#error-alert').text('Error: ' + xhr.responseText).fadeIn();
            $('#success-alert').hide();
        }
    });
});

function loadCategories() {
    const userRole = localStorage.getItem("userRole");

    if (userRole !== "ADMIN") {
        console.error("Unauthorized access!");
        return;
    }
    fetch("http://localhost:8080/api/v1/category/list")
        .then(response => response.json())
        .then(data => {
            const categoryList = document.getElementById("category-list");
            categoryList.innerHTML = "";

            if (data && data.data) {
                data.data.forEach(category => {
                    const categoryItem = document.createElement("div");
                    categoryItem.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mb-2');
                    categoryItem.innerHTML = `
                            <span>${category.name}</span>
                            <button class="btn-danger" onclick="deleteCategory(${category.id})">Delete</button>
                        `;
                    categoryList.appendChild(categoryItem);
                });
            }
        })
        .catch(error => console.error('Error fetching categories:', error));
}

function deleteCategory(id) {
    if (!id) {
        console.error('Category ID is undefined');
        return;
    }

    const yourAuthToken = localStorage.getItem('authToken');

    const categoryId = String(id);

    fetch(`/api/v1/category/delete/${categoryId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${yourAuthToken}`,
            'Content-Type': 'application/json'
        },
    })
        .then(response => {
            if (!response.ok) {
                console.error('Error deleting category:', response.statusText);
                return;
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                alert('Category deleted successfully!');
                loadCategories();
            } else {
                alert('Error deleting category: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Delete category request failed', error);
        });
}

window.onload = loadCategories;
