package com.example;

import java.util.HashSet;
import java.util.Set;

public class Whitelist
{
    private static final Set<String> ALLOWED_ITEMS = new HashSet<>();

    static
    {
        // Currency - always allowed
        ALLOWED_ITEMS.add("coins");
        ALLOWED_ITEMS.add("platinum token");

        // ─── AMMUNITION SLOT (Blessings only) ──────────────────
        ALLOWED_ITEMS.add("holy blessing");
        ALLOWED_ITEMS.add("peaceful blessing");
        ALLOWED_ITEMS.add("unholy blessing");
        ALLOWED_ITEMS.add("honourable blessing");
        ALLOWED_ITEMS.add("war blessing");
        ALLOWED_ITEMS.add("ancient blessing");
        ALLOWED_ITEMS.add("rada's blessing 1");
        ALLOWED_ITEMS.add("rada's blessing 2");
        ALLOWED_ITEMS.add("rada's blessing 3");
        ALLOWED_ITEMS.add("rada's blessing 4");
        ALLOWED_ITEMS.add("ghommal's lucky penny");
        ALLOWED_ITEMS.add("terrifying charm");
        ALLOWED_ITEMS.add("hallowed grapple");
        ALLOWED_ITEMS.add("mith grapple");
    }

    public static boolean isWhitelisted(String itemName)
    {
        if (itemName == null)
        {
            return false;
        }
        return ALLOWED_ITEMS.contains(itemName.toLowerCase());
    }

    public static boolean isDart(String itemName)
    {
        if (itemName == null)
        {
            return false;
        }
        String name = itemName.toLowerCase();
        return name.endsWith(" dart") || name.endsWith(" dart(p)")
                || name.endsWith(" dart(p+)") || name.endsWith(" dart(p++)");
    }

    public static void addItem(String itemName)
    {
        if (itemName != null && !itemName.isEmpty())
        {
            ALLOWED_ITEMS.add(itemName.toLowerCase());
        }
    }

    public static void removeItem(String itemName)
    {
        if (itemName != null)
        {
            ALLOWED_ITEMS.remove(itemName.toLowerCase());
        }
    }
}