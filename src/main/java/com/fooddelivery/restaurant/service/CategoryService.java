package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.CategoryDTO;
import com.fooddelivery.restaurant.repository.CategoryRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@lombok.extern.slf4j.Slf4j
public class CategoryService {
    @java.lang.SuppressWarnings("all")

    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categories", key = "#brandId != null ? #brandId.toString() : \'global\'")
    public List<CategoryDTO> getActiveCategories(java.util.UUID brandId) {
        List<com.fooddelivery.restaurant.entity.Category> categories;
        if (brandId != null) {
            categories = categoryRepository.findActiveCategoriesForBrand(brandId);
        } else {
            categories = categoryRepository.findByActiveTrueAndBrandIdIsNull();
        }
        return categories.stream().map(cat -> CategoryDTO.builder().id(cat.getId()).brandId(cat.getBrandId()).name(cat.getName()).description(cat.getDescription()).timings(cat.getTimings() != null ? cat.getTimings().stream().map(t -> CategoryDTO.CategoryTimingDTO.builder().openingTime(t.getOpeningTime()).closingTime(t.getClosingTime()).build()).collect(Collectors.toList()) : null).build()).collect(Collectors.toList());
    }

    @org.springframework.cache.annotation.CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO createCategory(CategoryDTO categoryDTO, java.util.UUID brandId) {
        com.fooddelivery.restaurant.entity.Category category = new com.fooddelivery.restaurant.entity.Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setBrandId(brandId);
        category.setActive(true);
        if (categoryDTO.getTimings() != null && !categoryDTO.getTimings().isEmpty()) {
            java.util.List<com.fooddelivery.restaurant.entity.CategoryTiming> timings = categoryDTO.getTimings().stream().map(dto -> {
                com.fooddelivery.restaurant.entity.CategoryTiming timing = new com.fooddelivery.restaurant.entity.CategoryTiming();
                timing.setCategory(category);
                timing.setOpeningTime(dto.getOpeningTime());
                timing.setClosingTime(dto.getClosingTime());
                return timing;
            }).collect(Collectors.toList());
            category.setTimings(timings);
        } else {
            com.fooddelivery.restaurant.entity.CategoryTiming defaultTiming = new com.fooddelivery.restaurant.entity.CategoryTiming();
            defaultTiming.setCategory(category);
            defaultTiming.setOpeningTime(java.time.LocalTime.of(9, 0));
            defaultTiming.setClosingTime(java.time.LocalTime.of(22, 0));
            category.setTimings(java.util.List.of(defaultTiming));
        }
        com.fooddelivery.restaurant.entity.Category saved = categoryRepository.save(category);
        return CategoryDTO.builder().id(saved.getId()).brandId(saved.getBrandId()).name(saved.getName()).description(saved.getDescription()).timings(saved.getTimings() != null ? saved.getTimings().stream().map(t -> CategoryDTO.CategoryTimingDTO.builder().openingTime(t.getOpeningTime()).closingTime(t.getClosingTime()).build()).collect(Collectors.toList()) : null).build();
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    @org.springframework.cache.annotation.CacheEvict(value = "categories", allEntries = true)
    public void clearCategoriesCacheOnStartup() {
        // Automatically evict categories cache when application starts up
        // This ensures Redis stays in sync with any Flyway DB migrations.
    }

    @org.springframework.cache.annotation.CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO updateCategory(java.util.UUID id, CategoryDTO categoryDTO) {
        com.fooddelivery.restaurant.entity.Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Category not found"));
        
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        
        if (categoryDTO.getTimings() != null) {
            category.getTimings().clear();
            java.util.List<com.fooddelivery.restaurant.entity.CategoryTiming> timings = categoryDTO.getTimings().stream().map(dto -> {
                com.fooddelivery.restaurant.entity.CategoryTiming timing = new com.fooddelivery.restaurant.entity.CategoryTiming();
                timing.setCategory(category);
                timing.setOpeningTime(dto.getOpeningTime());
                timing.setClosingTime(dto.getClosingTime());
                return timing;
            }).collect(Collectors.toList());
            category.getTimings().addAll(timings);
        }
        
        com.fooddelivery.restaurant.entity.Category saved = categoryRepository.save(category);
        return CategoryDTO.builder().id(saved.getId()).brandId(saved.getBrandId()).name(saved.getName()).description(saved.getDescription()).timings(saved.getTimings() != null ? saved.getTimings().stream().map(t -> CategoryDTO.CategoryTimingDTO.builder().openingTime(t.getOpeningTime()).closingTime(t.getClosingTime()).build()).collect(Collectors.toList()) : null).build();
    }

    @java.lang.SuppressWarnings("all")
    public CategoryService(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
