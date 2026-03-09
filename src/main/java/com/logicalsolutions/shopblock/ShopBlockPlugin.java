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

		int value = getItemValue(itemId);
		if (value >= config.valueThreshold())
		{
			event.consume();
			log.debug("Blocked sale of item {} (value: {})", itemId, value);

			if (config.showWarning())
			{
				String itemName = Text.removeTags(event.getMenuTarget());
				client.addChatMessage(
					ChatMessageType.GAMEMESSAGE,
					"",
					"Shop Block: " + itemName + " is worth " + formatValue(value) + " gp and cannot be sold.",
					null
				);
			}
		}
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
