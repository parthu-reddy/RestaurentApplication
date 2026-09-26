package com.fooddelivery.restaurant.service;

import com.fooddelivery.common.time.BusinessCalendar;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.function.Function;

/**
 * Whether an outlet, or one of its menu categories, is open at an instant.
 *
 * <p>Opening hours are wall-clock times ({@code 09:00–22:00}) in the outlet's own zone, so the
 * instant is converted to that zone's wall clock first. This used to be four hand-copied loops
 * against {@code LocalTime.now(ZoneId.of("Asia/Kolkata"))}, which was right for Bengaluru and wrong for
 * every other outlet. RandomDocuments/TimezoneCorrectness_2026-09-25, defect D3.
 *
 * <p>Both ends of a window are inclusive and a window whose close is before its open crosses midnight,
 * exactly as before ({@link BusinessCalendar#isWithin}).
 */
public final class OpeningHours {

    private OpeningHours() {}

    /** True if {@code now}, read on the outlet's wall clock, falls inside any of {@code windows}. */
    public static <T> boolean isOpen(Instant now, ZoneId outletZone, Collection<T> windows,
                                     Function<T, LocalTime> opens, Function<T, LocalTime> closes) {
        if (windows == null || windows.isEmpty()) {
            return false;
        }
        LocalTime wallClock = BusinessCalendar.localTime(now, outletZone);
        for (T window : windows) {
            if (BusinessCalendar.isWithin(wallClock, opens.apply(window), closes.apply(window))) {
                return true;
            }
        }
        return false;
    }
}
