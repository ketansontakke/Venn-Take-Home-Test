package com.example.demo.util;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

public class LoadUtils {

	public static BigDecimal parseAmount(String amount) {
		try {
			return new BigDecimal(amount.replace("$", ""));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid amount format: " + amount);
		}
	}

	public static Instant startOfDay(Instant instant) {
		return instant.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
	}

	public static Instant endOfDay(Instant instant) {
		return startOfDay(instant).plus(Duration.ofDays(1)).minusMillis(1);
	}

	public static Instant startOfWeek(Instant instant) {
		return instant.atZone(ZoneOffset.UTC).toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				.atStartOfDay(ZoneOffset.UTC).toInstant();
	}

	public static Instant endOfWeek(Instant instant) {
		return startOfWeek(instant).plus(Duration.ofDays(7)).minusMillis(1);
	}
}
