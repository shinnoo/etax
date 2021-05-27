package com.ptit.etax.common.enums;

public enum IncomePriceRule {
	FIRST(1, 0l, 5000000l, 0.05f),
	SECOND(2, 5000001l, 10000000l, 0.1f),
	THIRD(3, 10000001l, 18000000l, 0.15f),
	FOURTH(4, 18000001l, 32000000l, 0.2f),
	FIFTH(5, 32000001l, 52000000l, 0.25f),
	SIXTH(6, 52000001l, 80000000l, 0.3f),
	SEVENTH(7, 80000001l, Long.MAX_VALUE, 0.35f) // max = 9,223,372,036,854,775,807
	;


	int id;
	Long incomeFrom;
	Long incomeTo;
	float unitPrice;

	IncomePriceRule(int id, Long incomeFrom, Long incomeTo, float unitPrice) {
		this.id = id;
		this.incomeFrom = incomeFrom;
		this.incomeTo = incomeTo;
		this.unitPrice = unitPrice;
	}

	public Long priceOf(Long income) {
		if(income < this.incomeFrom) {
			return 0l;
		}

		return (long) ((Math.min(income, incomeTo) - incomeFrom + 1) * (unitPrice));
	}
}
