package com.corgam.cagedmobs.serializers.entity;

import net.minecraft.world.item.crafting.RecipeType;

public class RecipeAdditionalLoot implements RecipeType<AdditionalLootData> {
    @Override
    public String toString () {
        return "cagedmobs:additional_loot_data";
    }
}
