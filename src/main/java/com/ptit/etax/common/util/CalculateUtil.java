package com.ptit.etax.common.util;

import com.ptit.etax.common.enums.IncomePriceRule;

import java.util.stream.Stream;

public class CalculateUtil {
	public static Long calculate(Long totalIncome) {
		Long amount = totalIncome - 11000000l;	// Giảm trừ bản thân
		return Stream.of(IncomePriceRule.values())
				.map(rule -> rule.priceOf(amount))
				.reduce(0l, Long::sum);
	}
}
