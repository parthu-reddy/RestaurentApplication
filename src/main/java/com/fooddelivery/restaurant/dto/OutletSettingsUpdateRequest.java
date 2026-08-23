package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class OutletSettingsUpdateRequest {
        @NotNull
        private Integer defaultPrepTimeSeconds;

public OutletSettingsUpdateRequest() {
        }

public Integer getDefaultPrepTimeSeconds() {
            return this.defaultPrepTimeSeconds;
        }

public void setDefaultPrepTimeSeconds(final Integer defaultPrepTimeSeconds) {
            this.defaultPrepTimeSeconds = defaultPrepTimeSeconds;
        }

        @java.lang.Override
public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OutletSettingsUpdateRequest)) return false;
            final OutletSettingsUpdateRequest other = (OutletSettingsUpdateRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$defaultPrepTimeSeconds = this.getDefaultPrepTimeSeconds();
            final java.lang.Object other$defaultPrepTimeSeconds = other.getDefaultPrepTimeSeconds();
            if (this$defaultPrepTimeSeconds == null ? other$defaultPrepTimeSeconds != null : !this$defaultPrepTimeSeconds.equals(other$defaultPrepTimeSeconds)) return false;
            return true;
        }

protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OutletSettingsUpdateRequest;
        }

        @java.lang.Override
public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $defaultPrepTimeSeconds = this.getDefaultPrepTimeSeconds();
            result = result * PRIME + ($defaultPrepTimeSeconds == null ? 43 : $defaultPrepTimeSeconds.hashCode());
            return result;
        }

        @java.lang.Override
public java.lang.String toString() {
            return "OutletSettingsUpdateRequest(defaultPrepTimeSeconds=" + this.getDefaultPrepTimeSeconds() + ")";
        }
    }
