package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("resourceman")
public interface ResourcemanConfig extends Config
{
	@ConfigSection(
			name = "Enforcement",
			description = "Settings for what is blocked or warned",
			position = 0
	)
	String enforcementSection = "enforcement";

	@ConfigSection(
			name = "Notifications",
			description = "Settings for sounds and messages",
			position = 1
	)
	String notificationsSection = "notifications";

	@ConfigSection(
			name = "Appearance",
			description = "Settings for the plugin appearance",
			position = 2
	)
	String appearanceSection = "appearance";

	// ─── Enforcement ───────────────────────────────────────

	@ConfigItem(
			keyName = "blockShops",
			name = "Block NPC Shops",
			description = "Treat buying from NPC shops as a violation",
			section = enforcementSection,
			position = 0
	)
	default boolean blockShops()
	{
		return true;
	}

	// ─── Notifications ─────────────────────────────────────

	@ConfigItem(
			keyName = "playSound",
			name = "Play Sound on Violation",
			description = "Play the error sound when a violation is detected",
			section = notificationsSection,
			position = 0
	)
	default boolean playSound()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showChatMessage",
			name = "Show Chat Message on Violation",
			description = "Send a chat message when a violation is detected",
			section = notificationsSection,
			position = 1
	)
	default boolean showChatMessage()
	{
		return true;
	}

	// ─── Appearance ────────────────────────────────────────

	@ConfigItem(
			keyName = "pluginIcon",
			name = "Plugin Icon",
			description = "Choose the icon shown in the sidebar",
			section = appearanceSection,
			position = 0
	)
	default ResourcemanIcon pluginIcon()
	{
		return ResourcemanIcon.RUNE_PICKAXE;
	}
}