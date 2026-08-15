package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/internal/restaurants")
@lombok.extern.slf4j.Slf4j
public class InternalRestaurantController {
    @java.lang.SuppressWarnings("all")

    private final OutletRepository outletRepository;

    @GetMapping("/owner/{ownerId}/outlets")
    public ResponseEntity<List<String>> getOwnerOutlets(@PathVariable UUID ownerId) {
        List<String> outletIds = outletRepository.findByOwnerId(ownerId).stream().map(outlet -> outlet.getId().toString()).collect(Collectors.toList());
        return ResponseEntity.ok(outletIds);
    }

    @java.lang.SuppressWarnings("all")
    public InternalRestaurantController(final OutletRepository outletRepository) {
        this.outletRepository = outletRepository;
    }
}
