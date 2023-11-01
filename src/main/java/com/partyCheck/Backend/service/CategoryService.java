package com.partyCheck.Backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyCheck.Backend.entity.Category;
import com.partyCheck.Backend.model.CategoryDTO;
import com.partyCheck.Backend.repository.ICategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ICategoryRepository categoryRepository;
    private final ObjectMapper mapper;

    public void create(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category DTO cannot be null.");
        }

        Optional<Category> existingCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (existingCategory.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name already exists.");
        }

        Category category = mapper.convertValue(categoryDTO, Category.class);
        categoryRepository.save(category);
    }


}


