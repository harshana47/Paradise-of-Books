package org.example.landofbooks.controller;

import org.example.landofbooks.dto.CategoryDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.service.CategoryService;
import org.example.landofbooks.service.impl.UserServiceImpl;
import org.example.landofbooks.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/category")
@CrossOrigin("*")
public class AddCategoryController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CategoryService categoryService;
    private final ResponseDTO responseDTO;

    public AddCategoryController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, CategoryService categoryService, ResponseDTO responseDTO) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.categoryService = categoryService;
        this.responseDTO = responseDTO;
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseDTO> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();

            responseDTO.setCode(200);
            responseDTO.setMessage("Categories fetched successfully");
            responseDTO.setData(categories);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            responseDTO.setCode(500);
            responseDTO.setMessage("Error fetching categories: " + e.getMessage());
            responseDTO.setData(null);

            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable UUID id) {
        try {
            categoryService.deleteCategory(id);

            responseDTO.setCode(200);
            responseDTO.setMessage("Category deleted successfully");
            responseDTO.setData(null);

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            responseDTO.setCode(500);
            responseDTO.setMessage("Error deleting category: " + e.getMessage());
            responseDTO.setData(null);

            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            categoryService.saveCategory(categoryDTO);

            responseDTO.setCode(201);
            responseDTO.setMessage("Category saved successfully");
            responseDTO.setData(categoryDTO);

            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (Exception e) {
            if (e.getMessage().contains("Category with this name already exists")) {
                responseDTO.setCode(400);
                responseDTO.setMessage("Category with this name already exists.");
                responseDTO.setData(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            responseDTO.setCode(500);
            responseDTO.setMessage("Error saving category: " + e.getMessage());
            responseDTO.setData(null);

            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getByTitle")
    public ResponseEntity<ResponseDTO> getCategoryByTitle(@RequestParam String name) {
        name = name.trim();
        System.out.println("Requested category name: " + name);
        try {
            CategoryDTO category = categoryService.searchCategory(name);
            if (category != null) {
                responseDTO.setCode(200);
                responseDTO.setMessage("Category fetched successfully");
                responseDTO.setData(category);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                responseDTO.setCode(404);
                responseDTO.setMessage("Category not found with name: " + name);
                responseDTO.setData(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            responseDTO.setCode(500);
            responseDTO.setMessage("Error fetching category: " + e.getMessage());
            responseDTO.setData(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{categoryId}")
    public CategoryDTO getCategoryById(@PathVariable UUID categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

}
