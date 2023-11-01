package com.partyCheck.Backend.controller;

import com.partyCheck.Backend.entity.Category;
import com.partyCheck.Backend.model.CategoryDTO;
import com.partyCheck.Backend.repository.ICategoryRepository;
import com.partyCheck.Backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryRepository categoryRepository;
    private final CategoryService categoryService;
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCategoryNames() {
        List<String> categoryNames = categoryRepository.findAll().stream()
                .map(Category::getCategoryName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryNames);
    }

    @PostMapping
    public ResponseEntity<String> createCategory (@RequestBody CategoryDTO categoryDTO) {
        try {
            categoryService.create(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Category created");
        } catch (ResponseStatusException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getReason());
            }
            throw e;
        }
    }


}
