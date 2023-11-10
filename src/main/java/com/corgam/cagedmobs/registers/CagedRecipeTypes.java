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

    // Deferred registry system from newer versions
    //    public static final DeferredRegister<RecipeType<?>> CAGED_RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.BIOMES, CagedMobs.MOD_ID);
    //    public static final RegistryObject<RecipeType<EntityData>> ENTITY_RECIPE = CAGED_RECIPE_TYPES.register("entity_data", RecipeTypeEntityData::new);
    //    public static final RegistryObject<RecipeType<EnvironmentData>> ENVIRONMENT_RECIPE = CAGED_RECIPE_TYPES.register("environment_data", RecipeTypeEnvironmentData::new);
    //    public static final RegistryObject<RecipeType<AdditionalLootData>> ADDITIONAL_LOOT_RECIPE = CAGED_RECIPE_TYPES.register("additional_loot_data", RecipeAdditionalLoot::new);
}
