package com.corgam.cagedmobs.serializers.entity;

import net.minecraft.item.crafting.IRecipeType;

public class RecipeTypeEntityData implements IRecipeType<EntityData> {
    @Override
    public String toString () {
        return "cagedmobs:entity_data";
    }
}

