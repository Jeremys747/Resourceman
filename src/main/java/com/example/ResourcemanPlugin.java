package com.example;

import com.example.data.ResourceTracker;
import com.example.listeners.GrandExchangeListener;
import com.example.listeners.GroundItemListener;
import com.example.listeners.ShopListener;
import com.example.listeners.SkillResourceListener;
import com.example.listeners.TradeListener;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
		name = "Resourceman Mode",
		description = "Enforces self-obtained resources only"
)
public class ResourcemanPlugin extends Plugin
{
	public static final String VIOLATION_MESSAGE = "Resourcemen gather their own resources";
	public static final int VIOLATION_SOUND = 2277;
	private static final long VIOLATION_COOLDOWN_MS = 3000;

	@Inject
	private Client client;

	@Inject
	private ResourcemanConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private GrandExchangeListener grandExchangeListener;

	@Inject
	private TradeListener tradeListener;

	@Inject
	private GroundItemListener groundItemListener;

	@Inject
	private ShopListener shopListener;

	@Inject
	private SkillResourceListener skillResourceListener;

	private ResourceTracker resourceTracker;
	private ResourcemanPanel panel;
	private NavigationButton navButton;
	private long lastViolationTime = 0;

	public ResourceTracker getResourceTracker()
	{
		return resourceTracker;
	}

	public ResourcemanPanel getPanel()
	{
		return panel;
	}

	public ItemManager getItemManager()
	{
		return itemManager;
	}

	public void resetAllData()
	{
		configManager.unsetConfiguration("resourceman", "trackedResources");
		resourceTracker = new ResourceTracker(configManager);
		panel.update();
	}

	private BufferedImage buildIcon()
	{
		try
		{
			int itemId = config.pluginIcon().getItemId();
			BufferedImage image = itemManager.getImage(itemId);
			if (image != null)
			{
				return image;
			}
		}
		catch (Exception e)
		{
			log.debug("Failed to load icon", e);
		}
		return buildFallbackIcon();
	}

	private BufferedImage buildFallbackIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(new Color(180, 120, 40));
		g.fillRect(0, 0, 16, 16);
		g.setColor(Color.WHITE);
		g.drawString("R", 3, 12);
		g.dispose();
		return image;
	}

	private void updateIcon()
	{
		clientThread.invokeLater(() ->
		{
			BufferedImage icon = buildIcon();
			SwingUtilities.invokeLater(() ->
			{
				if (navButton == null)
				{
					return;
				}
				clientToolbar.removeNavigation(navButton);
				navButton = NavigationButton.builder()
						.tooltip("Resourceman Mode")
						.icon(icon)
						.priority(5)
						.panel(panel)
						.build();
				clientToolbar.addNavigation(navButton);
			});
		});
	}

	@Override
	protected void startUp() throws Exception
	{
		resourceTracker = new ResourceTracker(configManager);

		panel = new ResourcemanPanel(this);
		panel.update();

		navButton = NavigationButton.builder()
				.tooltip("Resourceman Mode")
				.icon(buildFallbackIcon())
				.priority(5)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);

		eventBus.register(grandExchangeListener);
		eventBus.register(tradeListener);
		eventBus.register(groundItemListener);
		eventBus.register(shopListener);
		eventBus.register(skillResourceListener);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			updateIcon();
		}

		log.debug("Resourceman Mode started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		eventBus.unregister(grandExchangeListener);
		eventBus.unregister(tradeListener);
		eventBus.unregister(groundItemListener);
		eventBus.unregister(shopListener);
		eventBus.unregister(skillResourceListener);
		skillResourceListener.reset();
		resourceTracker.resetSession();
		log.debug("Resourceman Mode stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			updateIcon();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("resourceman") && event.getKey().equals("pluginIcon"))
		{
			updateIcon();
		}
	}

	public void triggerViolation()
	{
		long now = System.currentTimeMillis();
		if (now - lastViolationTime < VIOLATION_COOLDOWN_MS)
		{
			return;
		}
		lastViolationTime = now;

		if (config.showChatMessage())
		{
			chatMessageManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.GAMEMESSAGE)
					.runeLiteFormattedMessage("<col=ff0000>" + VIOLATION_MESSAGE + "</col>")
					.build());
		}

		if (config.playSound())
		{
			client.playSoundEffect(VIOLATION_SOUND);
		}
	}

	@Provides
	ResourcemanConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ResourcemanConfig.class);
	}
}