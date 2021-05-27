package com.ptit.etax.util;

import com.ptit.etax.common.util.CalculateUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCalculator {

	@Test
	public void testCalculateTax(){
		assertEquals(0, CalculateUtil.calculate(500000l));
		assertEquals(0, CalculateUtil.calculate(500001l));
		assertEquals(0, CalculateUtil.calculate(9999999l));
		assertEquals(0, CalculateUtil.calculate(10000000l));
		assertEquals(0, CalculateUtil.calculate(10000001l));
		assertEquals(450000, CalculateUtil.calculate(18000000l));
		assertEquals(450000, CalculateUtil.calculate(18000001l));
		assertEquals(2550000, CalculateUtil.calculate(32000000l));
		assertEquals(2550000, CalculateUtil.calculate(32000001l));
		assertEquals(7000000, CalculateUtil.calculate(52000000l));
		assertEquals(7000000, CalculateUtil.calculate(52000001l));
		assertEquals(14850000, CalculateUtil.calculate(80000000l));
		assertEquals(14850000, CalculateUtil.calculate(80000001l));
		assertEquals(14850000, CalculateUtil.calculate(80000001l));
	}
}
