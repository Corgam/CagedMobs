package com.corgam.cagedmobs.serializers.env;

import com.corgam.cagedmobs.serializers.mob.MobData;
import net.minecraft.item.crafting.IRecipeType;

public class RecipeTypeEnvData implements IRecipeType<EnvironmentData> {

    @Override
    public String toString () {
        return "cagedmobs:env_data";
    }
}

