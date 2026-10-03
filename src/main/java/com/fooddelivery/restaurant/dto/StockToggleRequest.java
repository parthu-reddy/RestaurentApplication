package com.fooddelivery.restaurant.dto;

/** This request deliberately has no price or preparation-time fields. */
public record StockToggleRequest(@jakarta.validation.constraints.NotNull Boolean inStock) { }
