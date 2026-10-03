package com.fooddelivery.restaurant.security;

import com.fooddelivery.common.enums.OrganisationPermission;
import com.fooddelivery.common.security.organisation.OrganisationAccessPolicy;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("restaurantAccess")
@lombok.RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class RestaurantAccess {
    private final BrandRepository brands;
    private final OutletRepository outlets;
    private final OrganisationAccessPolicy organisations;

    public boolean onOrganisation(UUID organisationId, Authentication auth, OrganisationPermission permission) {
        return organisationId != null && organisations.can(auth, organisationId, permission);
    }
    public boolean onBrand(UUID brandId, Authentication auth, OrganisationPermission permission) {
        if (brandId == null) { return false; }
        return brands.findById(brandId).map(brand -> onOrganisation(brand.getOrganisationId(), auth, permission)).orElse(false);
    }
    public boolean onOutlet(UUID outletId, Authentication auth, OrganisationPermission permission) {
        if (outletId == null) { return false; }
        return outlets.findOrganisationByOutletId(outletId).map(outlet -> {
            boolean allowed = onOrganisation(outlet.organisationId(), auth, permission);
            if (!allowed) { log.info("Restaurant access refused outletId={} organisationId={} userId={}",
                    outletId, outlet.organisationId(), auth == null ? null : auth.getName()); }
            return allowed;
        }).orElse(false);
    }
}
