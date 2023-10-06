package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.env.RecipeTypeEnvData;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.serializers.mob.RecipeAdditionalLoot;
import com.corgam.cagedmobs.serializers.mob.RecipeTypeMobData;
import com.corgam.cagedmobs.setup.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CagedRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> CAGED_RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Constants.MOD_ID);

    public static final RegistryObject<RecipeType<MobData>> MOB_RECIPE = CAGED_RECIPE_TYPES.register("mob_data", RecipeTypeMobData::new);
    public static final RegistryObject<RecipeType<EnvironmentData>> ENV_RECIPE = CAGED_RECIPE_TYPES.register("env_data", RecipeTypeEnvData::new);
    public static final RegistryObject<RecipeType<AdditionalLootData>> ADDITIONAL_LOOT_RECIPE = CAGED_RECIPE_TYPES.register("additional_loot_data", RecipeAdditionalLoot::new);

//        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(RecipesHelper.ENV_RECIPE.toString()), RecipesHelper.ENV_RECIPE);
//        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(RecipesHelper.ADDITIONAL_LOOT_RECIPE.toString()), RecipesHelper.ADDITIONAL_LOOT_RECIPE);

}
