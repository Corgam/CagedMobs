package com.corgam.cagedmobs.serializers.environment;

import net.minecraft.world.item.crafting.RecipeType;

public class RecipeTypeEnvironmentData implements RecipeType<EnvironmentData> {

    @Override
    public String toString () {
        return "cagedmobs:env_data";
    }
}

