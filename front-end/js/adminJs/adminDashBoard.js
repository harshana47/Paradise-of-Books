
document.getElementById("logOut").addEventListener("click", function () {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userId");
    localStorage.removeItem("userEmail")

    window.location.href = "../index.html";
});
async function fetchDailyOrderStats() {
    try {
        const userRole = localStorage.getItem("userRole");

        if (userRole !== "ADMIN") {
            console.error("Unauthorized access!");
            return;
        }

        const authToken = localStorage.getItem("authToken");

        const response = await fetch("http://localhost:8080/api/v1/orders/daily-count", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${authToken}`
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();

        console.log(data);

        const labels = data.map(item => item.orderDate);
        const orderCounts = data.map(item => item.orderCount);
        const revenues = data.map(item => item.revenue);

        console.log("Order Counts: ", orderCounts);

        updateChart(labels, orderCounts, revenues);

        document.getElementById("totalOrders").textContent = orderCounts.reduce((acc, curr) => acc + curr, 0);
        document.getElementById("revenue").textContent = "$" + revenues.reduce((acc, curr) => acc + curr, 0).toFixed(2);
    } catch (error) {
        console.error("Error fetching daily order stats:", error);
    }
}


function updateChart(labels, orderCounts, revenues) {
    const ctx = document.getElementById('myChart');
    if (!ctx) {
        console.error("Canvas element not found!");
        return;
    }
    const context = ctx.getContext('2d');

    if (context) {
        new Chart(context, {
            type: "line",
            data: {
                labels: labels,
                datasets: [
                    {
                        label: "Daily Orders",
                        data: orderCounts,
                        borderColor: "blue",
                        fill: false
                    },
                    {
                        label: "Revenue (10%)",
                        data: revenues,
                        borderColor: "green",
                        fill: false
                    }
                ]
            }
        });
    }
}

document.addEventListener("DOMContentLoaded", function () {
    fetchDailyOrderStats();
});
