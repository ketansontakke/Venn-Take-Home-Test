package com.example.demo.util;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;

public class LoadUtils {

    public static BigDecimal parseAmount(String amount) {
        return new BigDecimal(amount.replace("$", ""));
    }

    public static Instant startOfDay(Instant instant) {
        return instant.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();
    }

    public static Instant endOfDay(Instant instant) {
        return startOfDay(instant).plus(Duration.ofDays(1)).minusMillis(1);
    }

    public static Instant startOfWeek(Instant instant) {
        return instant.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();
    }

    public static Instant endOfWeek(Instant instant) {
        return startOfWeek(instant).plus(Duration.ofDays(7)).minusMillis(1);
    }
}
