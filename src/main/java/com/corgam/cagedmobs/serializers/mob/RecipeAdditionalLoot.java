package com.corgam.cagedmobs.serializers.mob;

import net.minecraft.item.crafting.IRecipeType;

public class RecipeAdditionalLoot implements IRecipeType<AdditionalLootData> {
    @Override
    public String toString () {
        return "cagedmobs:additional_loot_data";
    }
}
