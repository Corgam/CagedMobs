package com.corgam.cagedmobs.serializers.environment;

import net.minecraft.item.crafting.IRecipeType;

public class RecipeTypeEnvironmentData implements IRecipeType<EnvironmentData> {

    @Override
    public String toString () {
        return "cagedmobs:env_data";
    }
}

