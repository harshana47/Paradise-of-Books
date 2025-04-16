package org.example.landofbooks.repo;

import org.example.landofbooks.dto.CategoryDTO;
import org.example.landofbooks.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, UUID> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Category findByName(@Param("name") String name);
}
