package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import com.corgam.cagedmobs.serializers.environment.EnvironmentDataSerializer;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootDataSerializer;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import com.corgam.cagedmobs.serializers.entity.EntityDataSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CagedRecipeSerializers {
    public static final DeferredRegister<IRecipeSerializer<?>> CAGED_RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CagedMobs.MOD_ID);

    public static final RegistryObject<IRecipeSerializer<EntityData>> ENTITY_RECIPE_SERIALIZER = CAGED_RECIPE_SERIALIZERS.register("entity_data", EntityDataSerializer::new);
    public static final RegistryObject<IRecipeSerializer<EnvironmentData>> ENVIRONMENT_RECIPE_SERIALIZER = CAGED_RECIPE_SERIALIZERS.register("environment_data", EnvironmentDataSerializer::new);
    public static final RegistryObject<IRecipeSerializer<AdditionalLootData>> ADDITIONAL_LOOT_RECIPE_SERIALIZER = CAGED_RECIPE_SERIALIZERS.register("additional_loot_data", AdditionalLootDataSerializer::new);

}
