package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.SetBrandCategoryTimingRequest;
import com.fooddelivery.restaurant.dto.TimingDTO;
import com.fooddelivery.restaurant.entity.BrandCategoryTiming;
import com.fooddelivery.restaurant.entity.Category;
import com.fooddelivery.restaurant.repository.BrandCategoryTimingRepository;
import com.fooddelivery.restaurant.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BrandCategoryTimingService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BrandCategoryTimingService.class);
    private final BrandCategoryTimingRepository brandCategoryTimingRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public List<TimingDTO> setTimings(UUID brandId, SetBrandCategoryTimingRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        brandCategoryTimingRepository.deleteByBrandIdAndCategoryId(brandId, request.getCategoryId());
        if (request.getTimings() != null && !request.getTimings().isEmpty()) {
            List<BrandCategoryTiming> timings = request.getTimings().stream().map(dto -> {
                return BrandCategoryTiming.builder().id(UUID.randomUUID()).brandId(brandId).category(category).openingTime(dto.getOpeningTime()).closingTime(dto.getClosingTime()).build();
            }).collect(Collectors.toList());
            brandCategoryTimingRepository.saveAll(timings);
            return request.getTimings();
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    public List<TimingDTO> getTimings(UUID brandId, UUID categoryId) {
        return brandCategoryTimingRepository.findByBrandIdAndCategoryId(brandId, categoryId).stream().map(t -> TimingDTO.builder().openingTime(t.getOpeningTime()).closingTime(t.getClosingTime()).build()).collect(Collectors.toList());
    }

    @java.lang.SuppressWarnings("all")
    public BrandCategoryTimingService(final BrandCategoryTimingRepository brandCategoryTimingRepository, final CategoryRepository categoryRepository) {
        this.brandCategoryTimingRepository = brandCategoryTimingRepository;
        this.categoryRepository = categoryRepository;
    }
}
