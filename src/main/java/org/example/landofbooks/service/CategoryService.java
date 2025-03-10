package org.example.landofbooks.service;

import org.example.landofbooks.dto.CategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    void saveCategory(CategoryDTO categoryDTO);

    List<CategoryDTO> getAllCategories();

    void deleteCategory(UUID id);

    CategoryDTO searchCategory(String name);
}
