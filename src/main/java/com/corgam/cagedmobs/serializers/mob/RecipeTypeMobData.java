package com.corgam.cagedmobs.serializers.mob;

import net.minecraft.world.item.crafting.RecipeType;

public class RecipeTypeMobData implements RecipeType<MobData> {
    @Override
    public String toString () {
        return "cagedmobs:mob_data";
    }
}

