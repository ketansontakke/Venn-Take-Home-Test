package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class LoadUtilsTest {

	@Test
	void shouldParseAmount() {
		BigDecimal amount = LoadUtils.parseAmount("$123.45");
		assertEquals(new BigDecimal("123.45"), amount);
	}
}