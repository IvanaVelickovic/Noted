package com.Noted.controller;

import com.Noted.dto.CategoryDTO;
import com.Noted.model.Category;
import com.Noted.response.CategoryNoteCount;
import com.Noted.response.CategoryResponse;
import com.Noted.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Tag(name = "Categories", description = "CRUD operations for categories, get note count by category")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "409", description = "Conflict - Category with that name already exists"),

    })
    @PostMapping("/add")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryDTO newCategory,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){

        Category category = categoryService.addCategory(authHeader.substring(7), newCategory.name());

        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.fromEntity(category));

    }

    @Operation(summary = "Get all categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved categories"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token")
    })
    @GetMapping("/get-all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){
        List<CategoryResponse> categories = categoryService.getAllCategories(authHeader.substring(7));

        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Update category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category successfully updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Not found - Category with that id cannot be found"),
            @ApiResponse(responseCode = "409", description = "Conflict - Category with that name already exists"),

    })
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@Valid @RequestBody CategoryDTO category,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                           @PathVariable Long id){
        Category updatedCategory = categoryService.updateCategory(category.name(), authHeader.substring(7), id);

        return ResponseEntity.ok(CategoryResponse.fromEntity(updatedCategory));
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/delete/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content - Category successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
            @ApiResponse(responseCode = "404", description = "Not found - Category with that id cannot be found"),
    })
    public ResponseEntity<Void> deleteCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
                                                           @PathVariable Long id){
        categoryService.deleteCategory(authHeader.substring(7), id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get note count for each category")
    @GetMapping("/note-count")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Note count by category successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT token"),
    })
    public ResponseEntity<List<CategoryNoteCount>> getNoteCountByCategory(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){

        List<CategoryNoteCount> categoryNoteCounts = categoryService.getNoteCountByCategory(authHeader.substring(7));

        return ResponseEntity.ok(categoryNoteCounts);

    }
}
