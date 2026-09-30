package com.smartsociety.complaint.service;

import com.smartsociety.complaint.entity.Category;
import com.smartsociety.complaint.exception.ResourceNotFoundException;
import com.smartsociety.complaint.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable("categories")
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.info("Fetching categories from database (cache miss)");
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }
}
