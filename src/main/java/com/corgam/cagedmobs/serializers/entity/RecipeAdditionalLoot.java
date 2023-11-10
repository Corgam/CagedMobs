package com.corgam.cagedmobs.serializers.entity;

import net.minecraft.item.crafting.IRecipeType;

public class RecipeAdditionalLoot implements IRecipeType<AdditionalLootData> {
    @Override
    public String toString () {
        return "cagedmobs:additional_loot_data";
    }
}
