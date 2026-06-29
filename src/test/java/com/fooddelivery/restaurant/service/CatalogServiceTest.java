package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.repository.IMenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private IMenuItemRepository menuItemRepository;

    @InjectMocks
    private CatalogService catalogService;

    private UUID restaurantId;
    private UUID itemId;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        menuItem = new MenuItem();
        menuItem.setId(itemId);
        menuItem.setRestaurantId(restaurantId);
        menuItem.setPrice(new BigDecimal("10.00"));
        menuItem.setName("Burger");
    }

    @Test
    void addMenuItem_ShouldSaveItem() {
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        MenuItem saved = catalogService.addMenuItem(restaurantId, menuItem);

        assertThat(saved).isNotNull();
        verify(menuItemRepository).save(menuItem);
        assertThat(menuItem.getRestaurantId()).isEqualTo(restaurantId);
    }

    @Test
    void addMenuItem_ShouldThrowExceptionForNegativePrice() {
        menuItem.setPrice(new BigDecimal("-5.00"));

        assertThatThrownBy(() -> catalogService.addMenuItem(restaurantId, menuItem))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price must be positive");
    }

    @Test
    void getMenuItems_ShouldReturnList() {
        when(menuItemRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(menuItem));

        List<MenuItem> items = catalogService.getMenuItems(restaurantId);

        assertThat(items).hasSize(1);
        verify(menuItemRepository).findByRestaurantId(restaurantId);
    }

    @Test
    void updateMenuItem_ShouldUpdateAndSave() {
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(menuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        MenuItem updateRequest = new MenuItem();
        updateRequest.setPrice(new BigDecimal("15.00"));
        updateRequest.setName("Cheese Burger");

        MenuItem updated = catalogService.updateMenuItem(restaurantId, itemId, updateRequest);

        assertThat(updated.getPrice()).isEqualTo(new BigDecimal("15.00"));
        assertThat(updated.getName()).isEqualTo("Cheese Burger");
        verify(menuItemRepository).save(menuItem);
    }

    @Test
    void updateMenuItem_ShouldThrowExceptionIfWrongRestaurant() {
        when(menuItemRepository.findById(itemId)).thenReturn(Optional.of(menuItem));
        
        UUID otherRestaurant = UUID.randomUUID();

        assertThatThrownBy(() -> catalogService.updateMenuItem(otherRestaurant, itemId, new MenuItem()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to this restaurant");
    }
}
