package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class OutletStatusUpdateRequest {
        @NotNull
        @com.fasterxml.jackson.annotation.JsonProperty("isActive")
        private Boolean isActive;

        @java.lang.SuppressWarnings("all")
        public OutletStatusUpdateRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public Boolean getIsActive() {
            return this.isActive;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("isActive")
        @java.lang.SuppressWarnings("all")
        public void setIsActive(final Boolean isActive) {
            this.isActive = isActive;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OutletStatusUpdateRequest)) return false;
            final OutletStatusUpdateRequest other = (OutletStatusUpdateRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$isActive = this.getIsActive();
            final java.lang.Object other$isActive = other.getIsActive();
            if (this$isActive == null ? other$isActive != null : !this$isActive.equals(other$isActive)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OutletStatusUpdateRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $isActive = this.getIsActive();
            result = result * PRIME + ($isActive == null ? 43 : $isActive.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OutletStatusUpdateRequest(isActive=" + this.getIsActive() + ")";
        }
    }
