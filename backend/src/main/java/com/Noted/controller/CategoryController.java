package com.Noted.controller;

import com.Noted.dto.CategoryDTO;
import com.Noted.model.Category;
import com.Noted.response.CategoryResponse;
import com.Noted.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryDTO newCategory,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){

        Category category = categoryService.addCategory(authHeader.substring(7), newCategory.name());

        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.fromEntity(category));

    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){
        List<CategoryResponse> categories = categoryService.getAllNotes(authHeader.substring(7));

        return ResponseEntity.ok(categories);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@Valid @RequestBody CategoryDTO category,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                           @PathVariable Long id){
        Category updatedCategory = categoryService.updateCategory(category.name(), authHeader.substring(7), id);

        return ResponseEntity.ok(CategoryResponse.fromEntity(updatedCategory));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                           @PathVariable Long id){
        categoryService.deleteCategory(authHeader.substring(7), id);

        return ResponseEntity.noContent().build();
    }
}
