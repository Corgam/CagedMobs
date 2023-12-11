package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import com.corgam.cagedmobs.serializers.environment.RecipeTypeEnvironmentData;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import com.corgam.cagedmobs.serializers.entity.RecipeAdditionalLoot;
import com.corgam.cagedmobs.serializers.entity.RecipeTypeEntityData;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CagedRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> CAGED_RECIPE_TYPES_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, CagedMobs.MOD_ID);

    public static final RegistryObject<RecipeType<EntityData>> ENTITY_RECIPE = CAGED_RECIPE_TYPES_REGISTER.register("entity_data", RecipeTypeEntityData::new);
    public static final RegistryObject<RecipeType<EnvironmentData>> ENVIRONMENT_RECIPE = CAGED_RECIPE_TYPES_REGISTER.register("environment_data", RecipeTypeEnvironmentData::new);
    public static final RegistryObject<RecipeType<AdditionalLootData>> ADDITIONAL_LOOT_RECIPE = CAGED_RECIPE_TYPES_REGISTER.register("additional_loot_data", RecipeAdditionalLoot::new);
}
