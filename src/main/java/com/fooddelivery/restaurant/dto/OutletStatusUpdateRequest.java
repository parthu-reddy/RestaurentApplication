package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@lombok.Data
public class OutletStatusUpdateRequest {
        @NotNull
        @com.fasterxml.jackson.annotation.JsonProperty("isActive")
        private Boolean isActive;


        @com.fasterxml.jackson.annotation.JsonProperty("isActive")
public void setIsActive(final Boolean isActive) {
            this.isActive = isActive;
        }




    }
