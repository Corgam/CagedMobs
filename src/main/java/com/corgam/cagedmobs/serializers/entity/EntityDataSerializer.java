package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EntityDataSerializer implements RecipeSerializer<EntityData> {

    public static final Codec<EntityData> CODEC =  RecordCodecBuilder.create((entityDataInstance) -> entityDataInstance
            .group(
                    // EntityID
                    ResourceLocation.CODEC.fieldOf("entity").forGetter(EntityData::getEntityID),
                    // Environments
                    Codec.list(Codec.STRING).fieldOf("environments").orElse(new ArrayList<>()).forGetter(EntityData::getEnvironments),
                    // Grow ticks
                    Codec.INT.flatXmap(growTicks -> growTicks > 0 ? DataResult.success(growTicks) : DataResult.error(() -> "EntityData recipe has an invalid growth tick. It must use a positive integer ( growTicks > 0)."), DataResult::success)
                            .fieldOf("growTicks")
                            .forGetter(EntityData::getTotalGrowTicks),
                    // Requires water
                    Codec.BOOL.fieldOf("requiresWater").orElse(false).forGetter(EntityData::ifRequiresWater),
                    // Loot data
                    Codec.list(LootData.CODEC).fieldOf("results").orElse(new ArrayList<>()).forGetter(EntityData::getResults),
                    // Tier
                    Codec.INT.flatXmap(tier -> (tier >= 1 && tier <= 3) ? DataResult.success(tier) : DataResult.error(() -> "EntityData recipe has an invalid sampler tier. It must use tiers: 1,2 or 3."), DataResult::success)
                            .fieldOf("samplerTier")
                            .forGetter(EntityData::getSamplerTier)
            ).apply(entityDataInstance, EntityData::new));

    @Override
    public Codec<EntityData> codec() {
        return EntityDataSerializer.CODEC;
    }

    @Override
    public @Nullable EntityData fromNetwork(FriendlyByteBuf pBuffer) {
        try {
            // Entity
            final ResourceLocation entityID = pBuffer.readResourceLocation();
            // Envs
            final List<String> validEnvs = new ArrayList<>();
            SerializationHelper.deserializeStringCollection(pBuffer, validEnvs);
            // Total grow ticks
            final int growTicks = pBuffer.readInt();
            // If the cage requires water
            final boolean requiresWater = pBuffer.readBoolean();
            // Loot data
            final List<LootData> results = new ArrayList<>();
            final int length = pBuffer.readInt();
            for (int i = 0; i < length; i++) {
                results.add(LootData.deserializeBuffer(pBuffer));
            }
            // Sampler tier
            final int tier = pBuffer.readInt();
            // Return object
            return new EntityData(entityID, validEnvs, growTicks, requiresWater, results, tier);
        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read entityData from network buffer.", e);
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, EntityData recipe) {
        try {
            // Entity
            buffer.writeResourceLocation(recipe.getEntityID());
            // Envs
            SerializationHelper.serializeStringCollection(buffer, recipe.getEnvironments());
            // Total  Grow Ticks
            buffer.writeInt(recipe.getTotalGrowTicks());
            // If the cage requires water
            buffer.writeBoolean(recipe.ifRequiresWater());
            // Loot data
            buffer.writeInt(recipe.getResults().size());
            for( final LootData data : recipe.getResults()){
                LootData.serializeBuffer(buffer, data);
            }
            // Sampler Tier
            buffer.writeInt(recipe.getSamplerTier());

        }catch (final Exception e) {
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to write entityData to the network buffer.",e);
        }
    }

    // Deserializes environments
    private static Set<String> deserializeEnvsData (ResourceLocation ownerId, JsonObject json) {
        final Set<String> categories = new HashSet<>();
        for (final JsonElement elem : json.getAsJsonArray("environments")) {
            categories.add(elem.getAsString().toLowerCase());
        }
        return categories;
    }

    // Deserializes loot data
    static List<LootData> deserializeLootData(ResourceLocation ownerId, JsonObject json, EntityType<?> entityType) {
        final List<LootData> loots = new ArrayList<>();
        for (final JsonElement elem : json.getAsJsonArray("results")) {
            if (elem.isJsonObject()) {
                final LootData lootData = LootData.deserialize(elem.getAsJsonObject());
                // Check for NBT data for item
                if(elem.getAsJsonObject().has("nbtName") && elem.getAsJsonObject().has("nbtData")){
                    ItemStack newItem = writeNBTtoItem(elem.getAsJsonObject().getAsJsonPrimitive("nbtName").getAsString(), elem.getAsJsonObject().getAsJsonPrimitive("nbtData").getAsString(), lootData.getItem());
                    lootData.setItem(newItem);
                }
                loots.add(lootData);
            }
        }
        return loots;
    }

    private static ItemStack writeNBTtoItem(String nbtName, String nbtData, ItemStack stack){
        CompoundTag nbt = new CompoundTag();
        nbt.putString(nbtName,nbtData);
        stack.setTag(nbt);
        return stack;
    }

}