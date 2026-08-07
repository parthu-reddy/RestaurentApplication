package com.fooddelivery.restaurant.security;

import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class RestaurantSecurityHelper {
    private final BrandRepository brandRepository;
    private final OutletRepository outletRepository;

    public boolean isBrandOwner(UUID brandId, String userId) {
        if (userId == null) {
            return false;
        }
        Brand brand = brandRepository.findById(brandId).orElse(null);
        if (brand == null || brand.getOwnerId() == null) {
            return false;
        }
        return brand.getOwnerId().toString().equals(userId);
    }

    public boolean isOutletOwner(UUID outletId, String userId) {
        if (userId == null) {
            return false;
        }
        Outlet outlet = outletRepository.findById(outletId).orElse(null);
        if (outlet == null) {
            return false;
        }
        return isBrandOwner(outlet.getBrandId(), userId);
    }

    @java.lang.SuppressWarnings("all")
    public RestaurantSecurityHelper(final BrandRepository brandRepository, final OutletRepository outletRepository) {
        this.brandRepository = brandRepository;
        this.outletRepository = outletRepository;
    }
}
