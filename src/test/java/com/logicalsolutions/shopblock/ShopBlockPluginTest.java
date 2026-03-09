package com.logicalsolutions.shopblock;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ShopBlockPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ShopBlockPlugin.class);
		RuneLite.main(args);
	}
}