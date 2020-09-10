package com.corgam.cagedmobs.serializers.mob;

import net.minecraft.item.crafting.IRecipeType;

public class RecipeTypeMobData implements IRecipeType<MobData> {

    @Override
    public String toString () {
        return "cagedmobs:mob_data";
    }
}

