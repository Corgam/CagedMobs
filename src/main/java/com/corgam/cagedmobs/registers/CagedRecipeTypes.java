package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import com.corgam.cagedmobs.serializers.entity.RecipeAdditionalLoot;
import com.corgam.cagedmobs.serializers.entity.RecipeTypeEntityData;
import com.corgam.cagedmobs.serializers.environment.RecipeTypeEnvironmentData;
import net.minecraft.world.item.crafting.RecipeType;

public class CagedRecipeTypes {

    public static final RecipeType<EntityData> ENTITY_RECIPE = new RecipeTypeEntityData();
    public static final RecipeType<EnvironmentData> ENVIRONMENT_RECIPE = new RecipeTypeEnvironmentData();
    public static final RecipeType<AdditionalLootData> ADDITIONAL_LOOT_RECIPE = new RecipeAdditionalLoot();

}
