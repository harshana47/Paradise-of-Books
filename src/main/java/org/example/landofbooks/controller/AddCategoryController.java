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
            // Fetch all categories from the service
            List<CategoryDTO> categories = categoryService.getAllCategories();

            // Set success response
            responseDTO.setCode(200);
            responseDTO.setMessage("Categories fetched successfully");
            responseDTO.setData(categories); // Return the list of categories

            // Return a success response
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            // Handle any errors
            responseDTO.setCode(500);
            responseDTO.setMessage("Error fetching categories: " + e.getMessage());
            responseDTO.setData(null);

            // Return an internal server error response
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable UUID id) {
        try {
            // Attempt to delete the category using the service layer
            categoryService.deleteCategory(id);

            // Set success response
            responseDTO.setCode(200);
            responseDTO.setMessage("Category deleted successfully");
            responseDTO.setData(null); // No data to return for a delete operation

            // Return a success response
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            // Handle errors
            responseDTO.setCode(500);
            responseDTO.setMessage("Error deleting category: " + e.getMessage());
            responseDTO.setData(null);

            // Return an internal server error response
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/save")
    public ResponseEntity<ResponseDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            // Attempt to save the category using the service layer
            categoryService.saveCategory(categoryDTO);

            // Set success response
            responseDTO.setCode(201);
            responseDTO.setMessage("Category saved successfully");
            responseDTO.setData(categoryDTO); // Return the saved category data

            // Return a success response
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (Exception e) {
            // Check if the exception is due to the category already existing
            if (e.getMessage().contains("Category with this name already exists")) {
                responseDTO.setCode(400);
                responseDTO.setMessage("Category with this name already exists.");
                responseDTO.setData(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            // Set error response for any unexpected errors
            responseDTO.setCode(500);
            responseDTO.setMessage("Error saving category: " + e.getMessage());
            responseDTO.setData(null);

            // Return an internal server error response
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getByTitle")
    public ResponseEntity<ResponseDTO> getCategoryByTitle(@RequestParam String name) {
        name = name.trim();  // Trim any leading/trailing spaces
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


}
