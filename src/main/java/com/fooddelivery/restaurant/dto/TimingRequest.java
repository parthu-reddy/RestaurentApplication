package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class TimingRequest {
        @NotNull
        private LocalTime openingTime;
        @NotNull
        private LocalTime closingTime;

        @java.lang.SuppressWarnings("all")
        public TimingRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public LocalTime getOpeningTime() {
            return this.openingTime;
        }

        @java.lang.SuppressWarnings("all")
        public LocalTime getClosingTime() {
            return this.closingTime;
        }

        @java.lang.SuppressWarnings("all")
        public void setOpeningTime(final LocalTime openingTime) {
            this.openingTime = openingTime;
        }

        @java.lang.SuppressWarnings("all")
        public void setClosingTime(final LocalTime closingTime) {
            this.closingTime = closingTime;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof TimingRequest)) return false;
            final TimingRequest other = (TimingRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$openingTime = this.getOpeningTime();
            final java.lang.Object other$openingTime = other.getOpeningTime();
            if (this$openingTime == null ? other$openingTime != null : !this$openingTime.equals(other$openingTime)) return false;
            final java.lang.Object this$closingTime = this.getClosingTime();
            final java.lang.Object other$closingTime = other.getClosingTime();
            if (this$closingTime == null ? other$closingTime != null : !this$closingTime.equals(other$closingTime)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof TimingRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $openingTime = this.getOpeningTime();
            result = result * PRIME + ($openingTime == null ? 43 : $openingTime.hashCode());
            final java.lang.Object $closingTime = this.getClosingTime();
            result = result * PRIME + ($closingTime == null ? 43 : $closingTime.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "TimingRequest(openingTime=" + this.getOpeningTime() + ", closingTime=" + this.getClosingTime() + ")";
        }
    }
