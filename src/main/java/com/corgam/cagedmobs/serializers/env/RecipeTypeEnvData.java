package com.corgam.cagedmobs.serializers.env;

import net.minecraft.world.item.crafting.RecipeType;

public class RecipeTypeEnvData implements RecipeType<EnvironmentData> {

    @Override
    public String toString () {
        return "cagedmobs:env_data";
    }
}

