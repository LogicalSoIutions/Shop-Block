package com.logicalsolutions.shopblock;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("shopblock")
public interface ShopBlockConfig extends Config
{
	@ConfigItem(
		keyName = "valueThreshold",
		name = "Value Threshold",
		description = "Items with a value at or above this amount (in GP) cannot be sold to shops",
		position = 1
	)
	default int valueThreshold()
	{
		return 10000;
	}

	@ConfigItem(
		keyName = "priceType",
		name = "Price Type",
		description = "Which price source to use for determining item value",
		position = 2
	)
	default PriceType priceType()
	{
		return PriceType.GE_PRICE;
	}

	@ConfigItem(
		keyName = "showWarning",
		name = "Show Warning",
		description = "Display a chat message when a sell attempt is blocked",
		position = 3
	)
	default boolean showWarning()
	{
		return true;
	}
}
