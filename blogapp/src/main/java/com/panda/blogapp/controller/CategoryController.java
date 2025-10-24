package com.panda.blogapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panda.blogapp.dto.CategoryDto;
import com.panda.blogapp.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Get list of all blog categories
    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // (Optional) Add category creation endpoint
    // @PostMapping
    // public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
    //     CategoryDto category = categoryService.createCategory(request);
    //     return new ResponseEntity<>(category, HttpStatus.CREATED);
    // }
}
