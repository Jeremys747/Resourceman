package com.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResourcemanIcon
{
    RUNE_PICKAXE("Rune pickaxe", 1275),
    DRAGON_PICKAXE("Dragon pickaxe", 11920),
    RUNE_AXE("Rune axe", 1359),
    DRAGON_AXE("Dragon axe", 6739),
    COOKED_LOBSTER("Cooked lobster", 379),
    COOKED_SHARK("Cooked shark", 385),
    GRIMY_RANARR("Grimy ranarr", 207),
    RUNITE_ORE("Runite ore", 451);

    private final String name;
    private final int itemId;

    @Override
    public String toString()
    {
        return name;
    }
}