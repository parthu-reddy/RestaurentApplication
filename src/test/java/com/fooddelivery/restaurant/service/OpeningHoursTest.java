package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.OutletTiming;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Defect D3 (RandomDocuments/TimezoneCorrectness_2026-09-25): opening hours were evaluated on an
 * Indian clock for every outlet. Runs in Pacific/Chatham, so no case can pass by the JVM zone
 * happening to agree.
 */
class OpeningHoursTest {

    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId KOLKATA = ZoneId.of("Asia/Kolkata");

    private static OutletTiming window(int open, int close) {
        OutletTiming t = new OutletTiming();
        t.setOpeningTime(LocalTime.of(open, 0));
        t.setClosingTime(LocalTime.of(close, 0));
        return t;
    }

    private static boolean open(Instant now, ZoneId zone, OutletTiming... windows) {
        return OpeningHours.isOpen(now, zone, List.of(windows), OutletTiming::getOpeningTime, OutletTiming::getClosingTime);
    }

    @Test
    void aNewYorkOutletKeepsNewYorkHours() {
        OutletTiming nineToFive = window(9, 17);
        // 13:00Z is 09:00 in New York (EDT) and 18:30 in Kolkata.
        assertTrue(open(Instant.parse("2026-09-25T13:00:00Z"), NEW_YORK, nineToFive));
        // 03:30Z is 09:00 in Kolkata and 23:30 the night before in New York.
        assertFalse(open(Instant.parse("2026-09-25T03:30:00Z"), NEW_YORK, nineToFive));
        assertTrue(open(Instant.parse("2026-09-25T03:30:00Z"), KOLKATA, nineToFive));
    }

    @Test
    void aLateNightWindowCrossesMidnightInTheOutletsZone() {
        OutletTiming lateNight = window(22, 2);
        assertTrue(open(Instant.parse("2026-09-25T04:30:00Z"), NEW_YORK, lateNight));   // 00:30 in New York
        assertFalse(open(Instant.parse("2026-09-25T16:00:00Z"), NEW_YORK, lateNight));  // 12:00 in New York
    }

    @Test
    void theWallClockFollowsDaylightSaving() {
        OutletTiming nineToFive = window(9, 17);
        // 13:30Z is 09:30 in New York in summer (EDT, -4) but 08:30 in winter (EST, -5).
        assertTrue(open(Instant.parse("2026-07-01T13:30:00Z"), NEW_YORK, nineToFive));
        assertFalse(open(Instant.parse("2026-12-01T13:30:00Z"), NEW_YORK, nineToFive));
    }

    @Test
    void noHoursMeansClosed() {
        assertFalse(OpeningHours.isOpen(Instant.now(), KOLKATA, List.<OutletTiming>of(), OutletTiming::getOpeningTime, OutletTiming::getClosingTime));
    }
}
