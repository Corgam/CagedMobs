package com.corgam.cagedmobs.serializers.entity;

import net.minecraft.world.item.crafting.RecipeType;

public class RecipeTypeEntityData implements RecipeType<EntityData> {
    @Override
    public String toString () {
        return "cagedmobs:entity_data";
    }
}

