package com.example;

import net.runelite.api.ItemComposition;

public class ItemRules
{
    private static final String[] BLOCKED_WIELDABLE = {
            "toktz-xil-ul"
    };

    public static boolean isAllowedItem(String itemName, ItemComposition comp)
    {
        if (itemName == null || itemName.equals("null") || itemName.isEmpty())
        {
            return true;
        }

        String lower = itemName.toLowerCase().trim();

        // Currency and blessings always allowed
        if (Whitelist.isWhitelisted(lower))
        {
            return true;
        }

        // Armour sets always allowed
        if (lower.contains(" set"))
        {
            return true;
        }

        // Darts always blocked
        if (Whitelist.isDart(lower))
        {
            return false;
        }

        // Arrows always blocked
        if (lower.endsWith("arrow") || lower.endsWith("arrows"))
        {
            return false;
        }

        // Bolts always blocked
        if (lower.endsWith("bolt") || lower.endsWith("bolts"))
        {
            return false;
        }

        // Throwing knives blocked
        if (lower.endsWith(" knife") || lower.endsWith(" knife(p)") ||
                lower.endsWith(" knife(p+)") || lower.endsWith(" knife(p++)"))
        {
            return false;
        }

        // Thrownaxes blocked
        if (lower.endsWith("thrownaxe"))
        {
            return false;
        }

        // Specific blocked wieldables
        for (String blocked : BLOCKED_WIELDABLE)
        {
            if (lower.equals(blocked))
            {
                return false;
            }
        }

        // Use item composition to check if it's equipment
        if (comp != null)
        {
            return isEquipmentByComposition(comp);
        }

        return false;
    }

    public static boolean isAllowedItem(String itemName)
    {
        if (itemName == null || itemName.equals("null") || itemName.isEmpty())
        {
            return true;
        }

        String lower = itemName.toLowerCase().trim();

        if (Whitelist.isWhitelisted(lower))
        {
            return true;
        }

        if (lower.contains(" set"))
        {
            return true;
        }

        if (Whitelist.isDart(lower))
        {
            return false;
        }

        if (lower.endsWith("arrow") || lower.endsWith("arrows"))
        {
            return false;
        }

        if (lower.endsWith("bolt") || lower.endsWith("bolts"))
        {
            return false;
        }

        if (lower.endsWith(" knife") || lower.endsWith(" knife(p)") ||
                lower.endsWith(" knife(p+)") || lower.endsWith(" knife(p++)"))
        {
            return false;
        }

        if (lower.endsWith("thrownaxe"))
        {
            return false;
        }

        for (String blocked : BLOCKED_WIELDABLE)
        {
            if (lower.equals(blocked))
            {
                return false;
            }
        }

        return false;
    }

    public static boolean isEquipmentByComposition(ItemComposition comp)
    {
        String[] actions = comp.getInventoryActions();
        if (actions == null)
        {
            return false;
        }
        for (String action : actions)
        {
            if (action == null) continue;
            if (action.equals("Wear") || action.equals("Wield") || action.equals("Equip"))
            {
                return true;
            }
        }
        return false;
    }
}