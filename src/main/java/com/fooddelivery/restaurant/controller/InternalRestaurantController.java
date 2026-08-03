package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.OutletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/internal/restaurants")
@RequiredArgsConstructor
public class InternalRestaurantController {

    private final OutletRepository outletRepository;

    @GetMapping("/owner/{ownerId}/outlets")
    public ResponseEntity<List<String>> getOwnerOutlets(@PathVariable UUID ownerId) {
        List<String> outletIds = outletRepository.findByOwnerId(ownerId).stream()
                .map(outlet -> outlet.getId().toString())
                .collect(Collectors.toList());
        return ResponseEntity.ok(outletIds);
    }
}
