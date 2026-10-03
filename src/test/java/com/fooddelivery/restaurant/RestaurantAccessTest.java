package com.fooddelivery.restaurant;

import com.fooddelivery.common.client.OrganisationServiceClient;
import com.fooddelivery.common.dto.organisation.MembershipDto;
import com.fooddelivery.common.dto.restaurant.OutletOrganisationDto;
import com.fooddelivery.common.enums.*;
import com.fooddelivery.common.security.organisation.DefaultOrganisationAccessPolicy;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.repository.*;
import com.fooddelivery.restaurant.security.RestaurantAccess;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Independent permission table and actual shared membership policy, including organisation resolution. */
class RestaurantAccessTest {
    private final BrandRepository brands=mock(BrandRepository.class);
    private final OutletRepository outlets=mock(OutletRepository.class);
    private final OrganisationServiceClient identity=mock(OrganisationServiceClient.class);
    private final RestaurantAccess access=new RestaurantAccess(brands,outlets,
        new DefaultOrganisationAccessPolicy(identity,new SimpleMeterRegistry()));
    private final UUID user=UUID.randomUUID(),org=UUID.randomUUID(),brand=UUID.randomUUID(),outlet=UUID.randomUUID();
    private final UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(user.toString(),null,
        List.of(new SimpleGrantedAuthority("ROLE_RESTAURANT")));

    private void ownedResources() {
        var b=new Brand();b.setId(brand);b.setOrganisationId(org);
        when(brands.findById(brand)).thenReturn(Optional.of(b));
        when(outlets.findOrganisationByOutletId(outlet)).thenReturn(Optional.of(new OutletOrganisationDto(outlet,brand,org)));
    }
    @ParameterizedTest @EnumSource(OrganisationRole.class)
    void everyBrandAndOutletPermissionUsesTheMembersOrganisationRole(OrganisationRole role) {
        ownedResources();when(identity.getMembership(org,user)).thenReturn(
            new MembershipDto(org,OrganisationStatus.ACTIVE,user,role,MembershipStatus.ACTIVE));
        var permitted=EnumSet.of(OrganisationPermission.ORG_VIEW,OrganisationPermission.STOCK_TOGGLE,OrganisationPermission.ORDERS_OPERATE);
        if(role!=OrganisationRole.STAFF)permitted.addAll(EnumSet.of(OrganisationPermission.OUTLET_MANAGE,
            OrganisationPermission.MENU_MANAGE,OrganisationPermission.EARNINGS_VIEW,OrganisationPermission.ADS_VIEW,
            OrganisationPermission.ADS_MANAGE,OrganisationPermission.WALLET_VIEW));
        if(role==OrganisationRole.ADMIN || role==OrganisationRole.OWNER)permitted.addAll(EnumSet.of(
            OrganisationPermission.MEMBERS_MANAGE,OrganisationPermission.BUSINESS_APPLY,
            OrganisationPermission.PAYOUTS_MANAGE,OrganisationPermission.WALLET_TOPUP));
        if(role==OrganisationRole.OWNER)permitted.add(OrganisationPermission.ORG_MANAGE);
        for(var permission:OrganisationPermission.values()){
            assertEquals(permitted.contains(permission),access.onBrand(brand,auth,permission),role+" brand "+permission);
            assertEquals(permitted.contains(permission),access.onOutlet(outlet,auth,permission),role+" outlet "+permission);
        }
        verify(identity,times(1)).getMembership(org,user);
    }
    @Test void aMembershipForAnotherOrganisationCannotGrantAnyPermission() {
        ownedResources();when(identity.getMembership(org,user)).thenReturn(new MembershipDto(UUID.randomUUID(),
            OrganisationStatus.ACTIVE,user,OrganisationRole.OWNER,MembershipStatus.ACTIVE));
        for(var permission:OrganisationPermission.values()){
            assertFalse(access.onBrand(brand,auth,permission));assertFalse(access.onOutlet(outlet,auth,permission));
        }
    }
    @Test void missingResourcesAndUnprovisionedMemberNeverAcquireAccess() {
        assertFalse(access.onBrand(null,auth,OrganisationPermission.ORG_VIEW));
        assertFalse(access.onOutlet(null,auth,OrganisationPermission.ORG_VIEW));
        assertFalse(access.onBrand(brand,auth,OrganisationPermission.ORG_VIEW));
        assertFalse(access.onOutlet(outlet,auth,OrganisationPermission.ORG_VIEW));
        verifyNoInteractions(identity);
        ownedResources();when(identity.getMembership(org,user)).thenReturn(null);
        assertFalse(access.onBrand(brand,auth,OrganisationPermission.MENU_MANAGE));
        assertFalse(access.onOutlet(outlet,auth,OrganisationPermission.STOCK_TOGGLE));
    }
}
