package com.example.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.runelite.client.config.ConfigManager;

public class ResourceTracker
{
    private static final String CONFIG_GROUP = "resourceman";
    private static final String RESOURCES_KEY = "trackedResources";

    private final ConfigManager configManager;

    private Map<String, Integer> allTimeResources = new HashMap<>();
    private Map<String, Integer> sessionResources = new HashMap<>();

    public ResourceTracker(ConfigManager configManager)
    {
        this.configManager = configManager;
        load();
    }

    public void trackResource(String itemName, int quantity)
    {
        if (itemName == null || itemName.isEmpty())
        {
            return;
        }

        String name = itemName.toLowerCase();
        allTimeResources.merge(name, quantity, Integer::sum);
        sessionResources.merge(name, quantity, Integer::sum);
        save();
    }

    public Map<String, Integer> getAllTimeResources()
    {
        return allTimeResources.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Integer> getSessionResources()
    {
        return sessionResources.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public int getAllTimeResourceCount()
    {
        return allTimeResources.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getSessionResourceCount()
    {
        return sessionResources.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void save()
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : allTimeResources.entrySet())
        {
            if (sb.length() > 0) sb.append(",");
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }
        configManager.setConfiguration(CONFIG_GROUP, RESOURCES_KEY, sb.toString());
    }

    private void load()
    {
        String resourceData = configManager.getConfiguration(CONFIG_GROUP, RESOURCES_KEY);
        if (resourceData != null && !resourceData.isEmpty())
        {
            for (String entry : resourceData.split(","))
            {
                String[] parts = entry.split(":");
                if (parts.length == 2)
                {
                    try
                    {
                        allTimeResources.put(parts[0], Integer.parseInt(parts[1]));
                    }
                    catch (NumberFormatException e)
                    {
                        // Skip
                    }
                }
            }
        }
    }

    public void resetSession()
    {
        sessionResources.clear();
    }
}