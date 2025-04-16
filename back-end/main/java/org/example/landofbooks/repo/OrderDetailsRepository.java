package org.example.landofbooks.repo;

import org.example.landofbooks.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails , UUID> {

    @Query(value = "SELECT DATE(order_date) AS order_date, COUNT(*) AS order_count, SUM(price) * 0.10 AS revenue " +
            "FROM order_details " +
            "WHERE order_date >= CURDATE() - INTERVAL 7 DAY " +
            "GROUP BY DATE(order_date)", nativeQuery = true)
    List<Object[]> getDailyOrdersWithRevenue();

}
