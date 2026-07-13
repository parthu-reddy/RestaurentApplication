package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.CategoryDTO;
import com.fooddelivery.restaurant.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Cacheable("categories")
    public List<CategoryDTO> getActiveCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(cat -> CategoryDTO.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .description(cat.getDescription())
                        .timings(cat.getTimings() != null ? cat.getTimings().stream()
                                .map(t -> CategoryDTO.CategoryTimingDTO.builder()
                                        .openingTime(t.getOpeningTime())
                                        .closingTime(t.getClosingTime())
                                        .build())
                                .collect(Collectors.toList()) : null)
                        .build())
                .collect(Collectors.toList());
    }

    @org.springframework.cache.annotation.CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        com.fooddelivery.restaurant.entity.Category category = new com.fooddelivery.restaurant.entity.Category();
        category.setId(java.util.UUID.randomUUID());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setActive(true);

        if (categoryDTO.getTimings() != null && !categoryDTO.getTimings().isEmpty()) {
            java.util.List<com.fooddelivery.restaurant.entity.CategoryTiming> timings = categoryDTO.getTimings().stream().map(dto -> {
                com.fooddelivery.restaurant.entity.CategoryTiming timing = new com.fooddelivery.restaurant.entity.CategoryTiming();
                timing.setId(java.util.UUID.randomUUID());
                timing.setCategory(category);
                timing.setOpeningTime(dto.getOpeningTime());
                timing.setClosingTime(dto.getClosingTime());
                timing.setCreatedAt(java.time.LocalDateTime.now());
                timing.setUpdatedAt(java.time.LocalDateTime.now());
                return timing;
            }).collect(Collectors.toList());
            category.setTimings(timings);
        } else {
            com.fooddelivery.restaurant.entity.CategoryTiming defaultTiming = new com.fooddelivery.restaurant.entity.CategoryTiming();
            defaultTiming.setId(java.util.UUID.randomUUID());
            defaultTiming.setCategory(category);
            defaultTiming.setOpeningTime(java.time.LocalTime.MIN);
            defaultTiming.setClosingTime(java.time.LocalTime.of(23, 59, 59));
            defaultTiming.setCreatedAt(java.time.LocalDateTime.now());
            defaultTiming.setUpdatedAt(java.time.LocalDateTime.now());
            category.setTimings(List.of(defaultTiming));
        }

        com.fooddelivery.restaurant.entity.Category saved = categoryRepository.save(category);

        return CategoryDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .timings(saved.getTimings() != null ? saved.getTimings().stream()
                        .map(t -> CategoryDTO.CategoryTimingDTO.builder()
                                .openingTime(t.getOpeningTime())
                                .closingTime(t.getClosingTime())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    @org.springframework.cache.annotation.CacheEvict(value = "categories", allEntries = true)
    public void clearCategoriesCacheOnStartup() {
        // Automatically evict categories cache when application starts up
        // This ensures Redis stays in sync with any Flyway DB migrations.
    }
}
