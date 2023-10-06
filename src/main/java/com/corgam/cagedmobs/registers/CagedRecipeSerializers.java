package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.env.EnvironmentDataSerializer;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootDataSerializer;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.serializers.mob.MobDataSerializer;
import com.corgam.cagedmobs.setup.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CagedRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> CAGED_RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

    public static final RegistryObject<RecipeSerializer<MobData>> MOB_RECIPE_SERIALIZER = CAGED_RECIPE_SERIALIZERS.register("mob_data", MobDataSerializer::new);
    public static final RegistryObject<RecipeSerializer<EnvironmentData>> ENV_RECIPE_SERIALIZER = CAGED_RECIPE_SERIALIZERS.register("env_data", EnvironmentDataSerializer::new);
    public static final RegistryObject<RecipeSerializer<AdditionalLootData>> ADDITIONAL_LOOT_RECIPE_SERIALIZER = CAGED_RECIPE_SERIALIZERS.register("additional_loot_data", AdditionalLootDataSerializer::new);

}
