package com.logicalsolutions.shopblock;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Shop Block"
)
public class ShopBlockPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ShopBlockConfig config;

	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("Shop Block started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("Shop Block stopped!");
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		String option = event.getMenuOption();
		if (option == null || !option.startsWith("Sell"))
		{
			return;
		}

		int itemId = event.getItemId();
		if (itemId < 0)
		{
			return;
		}

		String itemName = Text.removeTags(event.getMenuTarget()).trim();
		if (isItemExcepted(itemName, config.exceptions()))
		{
			log.debug("Allowed sale of excepted item {}", itemName);
			return;
		}

		int value = getItemValue(itemId);
		if (value >= config.valueThreshold())
		{
			event.consume();
			log.debug("Blocked sale of item {} (value: {})", itemId, value);

			if (config.showWarning())
			{
				client.addChatMessage(
					ChatMessageType.GAMEMESSAGE,
					"",
					"Shop Block: " + itemName + " is worth " + formatValue(value) + " gp and cannot be sold.",
					null
				);
			}
		}
	}

	static boolean isItemExcepted(String itemName, String exceptions)
	{
		if (itemName == null || exceptions == null || exceptions.trim().isEmpty())
		{
			return false;
		}

		String normalizedItemName = normalizeItemName(itemName);
		for (String exception : exceptions.split("[\\r\\n,]+"))
		{
			String normalizedException = normalizeItemName(exception);
			if (normalizedException.isEmpty())
			{
				continue;
			}

			if (matchesException(normalizedItemName, normalizedException))
			{
				return true;
			}
		}

		return false;
	}

	private static boolean matchesException(String itemName, String exception)
	{
		int wildcardIndex = exception.indexOf('*');
		if (wildcardIndex == -1)
		{
			return itemName.equals(exception);
		}

		int itemNameIndex = 0;
		boolean anchoredAtStart = wildcardIndex != 0;
		boolean anchoredAtEnd = exception.charAt(exception.length() - 1) != '*';

		for (String part : exception.split("\\*", -1))
		{
			if (part.isEmpty())
			{
				continue;
			}

			int matchIndex = itemName.indexOf(part, itemNameIndex);
			if (matchIndex == -1 || anchoredAtStart && itemNameIndex == 0 && matchIndex != 0)
			{
				return false;
			}

			itemNameIndex = matchIndex + part.length();
		}

		return !anchoredAtEnd || itemNameIndex == itemName.length();
	}

	private static String normalizeItemName(String itemName)
	{
		return itemName.trim().replaceAll("\\s+", " ").toLowerCase();
	}

	private int getItemValue(int itemId)
	{
		if (config.priceType() == PriceType.GE_PRICE)
		{
			return itemManager.getItemPrice(itemId);
		}
		return client.getItemDefinition(itemId).getHaPrice();
	}

	private String formatValue(int value)
	{
		if (value >= 1_000_000)
		{
			return String.format("%.1fM", value / 1_000_000.0);
		}
		if (value >= 1_000)
		{
			return String.format("%.1fK", value / 1_000.0);
		}
		return String.valueOf(value);
	}

	@Provides
	ShopBlockConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ShopBlockConfig.class);
	}
}
