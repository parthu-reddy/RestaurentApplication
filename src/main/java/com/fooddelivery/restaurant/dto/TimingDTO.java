package com.fooddelivery.restaurant.dto;

import java.time.LocalTime;

public class TimingDTO {
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime openingTime;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime closingTime;


    @java.lang.SuppressWarnings("all")
    public static class TimingDTOBuilder {
        @java.lang.SuppressWarnings("all")
        private LocalTime openingTime;
        @java.lang.SuppressWarnings("all")
        private LocalTime closingTime;

        @java.lang.SuppressWarnings("all")
        TimingDTOBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        @java.lang.SuppressWarnings("all")
        public TimingDTO.TimingDTOBuilder openingTime(final LocalTime openingTime) {
            this.openingTime = openingTime;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        @java.lang.SuppressWarnings("all")
        public TimingDTO.TimingDTOBuilder closingTime(final LocalTime closingTime) {
            this.closingTime = closingTime;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public TimingDTO build() {
            return new TimingDTO(this.openingTime, this.closingTime);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "TimingDTO.TimingDTOBuilder(openingTime=" + this.openingTime + ", closingTime=" + this.closingTime + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static TimingDTO.TimingDTOBuilder builder() {
        return new TimingDTO.TimingDTOBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public LocalTime getOpeningTime() {
        return this.openingTime;
    }

    @java.lang.SuppressWarnings("all")
    public LocalTime getClosingTime() {
        return this.closingTime;
    }

    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @java.lang.SuppressWarnings("all")
    public void setOpeningTime(final LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @java.lang.SuppressWarnings("all")
    public void setClosingTime(final LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TimingDTO)) return false;
        final TimingDTO other = (TimingDTO) o;
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
        return other instanceof TimingDTO;
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
        return "TimingDTO(openingTime=" + this.getOpeningTime() + ", closingTime=" + this.getClosingTime() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public TimingDTO() {
    }

    @java.lang.SuppressWarnings("all")
    public TimingDTO(final LocalTime openingTime, final LocalTime closingTime) {
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
}
