package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import com.corgam.cagedmobs.serializers.environment.RecipeTypeEnvironmentData;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import com.corgam.cagedmobs.serializers.entity.RecipeAdditionalLoot;
import com.corgam.cagedmobs.serializers.entity.RecipeTypeEntityData;
import net.minecraft.item.crafting.IRecipeType;

public class CagedRecipeTypes {

    public static final IRecipeType<EntityData> ENTITY_RECIPE = new RecipeTypeEntityData();
    public static final IRecipeType<EnvironmentData> ENVIRONMENT_RECIPE = new RecipeTypeEnvironmentData();
    public static final IRecipeType<AdditionalLootData> ADDITIONAL_LOOT_RECIPE = new RecipeAdditionalLoot();

}
