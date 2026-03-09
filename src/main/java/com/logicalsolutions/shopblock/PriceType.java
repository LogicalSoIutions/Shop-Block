package com.logicalsolutions.shopblock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceType
{
	GE_PRICE("GE Price"),
	HA_VALUE("HA Value");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
