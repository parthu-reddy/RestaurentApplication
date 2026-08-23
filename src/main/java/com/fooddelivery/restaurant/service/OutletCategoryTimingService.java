package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.SetOutletCategoryTimingRequest;
import com.fooddelivery.restaurant.dto.TimingDTO;
import com.fooddelivery.restaurant.entity.Category;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.OutletCategoryTiming;
import com.fooddelivery.restaurant.repository.CategoryRepository;
import com.fooddelivery.restaurant.repository.OutletCategoryTimingRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@lombok.extern.slf4j.Slf4j
public class OutletCategoryTimingService {
private final OutletCategoryTimingRepository outletCategoryTimingRepository;
    private final OutletRepository outletRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public List<TimingDTO> setTimings(UUID outletId, SetOutletCategoryTimingRequest request) {
        Outlet outlet = outletRepository.findById(outletId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outlet not found"));
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        outletCategoryTimingRepository.deleteByOutletIdAndCategoryId(outletId, request.getCategoryId());
        if (request.getTimings() != null && !request.getTimings().isEmpty()) {
            List<OutletCategoryTiming> timings = request.getTimings().stream().map(dto -> {
                return OutletCategoryTiming.builder().id(UUID.randomUUID()).outlet(outlet).category(category).openingTime(dto.getOpeningTime()).closingTime(dto.getClosingTime()).build();
            }).collect(Collectors.toList());
            outletCategoryTimingRepository.saveAll(timings);
            return request.getTimings();
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    public List<TimingDTO> getTimings(UUID outletId, UUID categoryId) {
        return outletCategoryTimingRepository.findByOutletIdAndCategoryId(outletId, categoryId).stream().map(t -> TimingDTO.builder().openingTime(t.getOpeningTime()).closingTime(t.getClosingTime()).build()).collect(Collectors.toList());
    }

public OutletCategoryTimingService(final OutletCategoryTimingRepository outletCategoryTimingRepository, final OutletRepository outletRepository, final CategoryRepository categoryRepository) {
        this.outletCategoryTimingRepository = outletCategoryTimingRepository;
        this.outletRepository = outletRepository;
        this.categoryRepository = categoryRepository;
    }
}
