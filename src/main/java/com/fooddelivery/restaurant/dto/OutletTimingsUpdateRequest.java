package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class OutletTimingsUpdateRequest {
        @NotNull
        private List<TimingRequest> timings;

public OutletTimingsUpdateRequest() {
        }

public List<TimingRequest> getTimings() {
            return this.timings;
        }

public void setTimings(final List<TimingRequest> timings) {
            this.timings = timings;
        }

        @java.lang.Override
public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OutletTimingsUpdateRequest)) return false;
            final OutletTimingsUpdateRequest other = (OutletTimingsUpdateRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$timings = this.getTimings();
            final java.lang.Object other$timings = other.getTimings();
            if (this$timings == null ? other$timings != null : !this$timings.equals(other$timings)) return false;
            return true;
        }

protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OutletTimingsUpdateRequest;
        }

        @java.lang.Override
public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $timings = this.getTimings();
            result = result * PRIME + ($timings == null ? 43 : $timings.hashCode());
            return result;
        }

        @java.lang.Override
public java.lang.String toString() {
            return "OutletTimingsUpdateRequest(timings=" + this.getTimings() + ")";
        }
    }
