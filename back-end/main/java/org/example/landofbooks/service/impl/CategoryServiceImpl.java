package org.example.landofbooks.service.impl;

import org.example.landofbooks.dto.CategoryDTO;
import org.example.landofbooks.dto.UserDTO;
import org.example.landofbooks.entity.Category;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.CategoryRepo;
import org.example.landofbooks.service.CategoryService;
import org.example.landofbooks.util.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE,RequestMethod.OPTIONS})
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void saveCategory(CategoryDTO categoryDTO) {
        if (categoryRepo.existsByName(categoryDTO.getName())){
            throw new RuntimeException("Customer already exists");
        }
        Category category = modelMapper.map(categoryDTO, Category.class);
        categoryRepo.save(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return modelMapper.map(categoryRepo.findAll(), new TypeToken<List<CategoryDTO>>() {}.getType());
    }

    @Override
    public void deleteCategory(UUID id) {
        if (!categoryRepo.existsById(id)) {
            throw new RuntimeException("Category with ID " + id + " does not exist");
        }
        categoryRepo.deleteById(id);
    }

    @Override
    public CategoryDTO searchCategory(String name) {
        System.out.println("Searching for category: " + name);
        boolean categoryExists = categoryRepo.existsByName(name);
        System.out.println("Category exists: " + categoryExists);

        if (categoryExists) {
            Category category = categoryRepo.findByName(name);
            if (category != null) {
                System.out.println("Category found: " + category.getName());
                return new CategoryDTO(category.getCid(), category.getName());
            } else {
                System.out.println("Category found but it is null");
            }
        } else {
            System.out.println("No category found with name: " + name);
        }
        return null;
    }

    @Override
    public CategoryDTO getCategoryById(UUID categoryId) {
        Optional<Category> categoryOpt = categoryRepo.findById(categoryId);

        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            return new CategoryDTO(category.getCid(), category.getName());
        }

        return null;
    }
}
