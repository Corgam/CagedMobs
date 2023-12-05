package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.*;

public class EntityDataSerializer implements RecipeSerializer<EntityData> {

    public static final Codec<EntityData> CODEC =  RecordCodecBuilder.create((entityDataInstance) -> entityDataInstance
            .group(
                // EntityID
                Codec.STRING.fieldOf("entity").forGetter(EntityData::getEntityId),
                // Environments
                Codec.list(Codec.STRING).fieldOf("environments").orElse(new ArrayList<>()).forGetter(EntityData::getEnvironments),
                // Grow ticks
                Codec.INT.flatXmap(growTicks -> growTicks > 0 ? DataResult.success(growTicks) : DataResult.error(() -> "EntityData recipe has an invalid growth tick. It must use a positive integer ( growTicks > 0)."), DataResult::success)
                        .fieldOf("growTicks")
                        .forGetter(EntityData::getTotalGrowTicks),
                // Requires water
                Codec.BOOL.fieldOf("requiresWater").orElse(false).forGetter(EntityData::ifRequiresWater),
                // Loot data
                Codec.list(LootData.CODEC).fieldOf("results").forGetter(EntityData::getResults),
                // Tier
                Codec.INT.flatXmap(tier -> (tier >= 1 && tier <= 3) ? DataResult.success(tier) : DataResult.error(() -> "EntityData recipe has an invalid sampler tier. It must use tiers: 1,2 or 3."), DataResult::success)
                        .fieldOf("samplerTier")
                        .forGetter(EntityData::getSamplerTier)
            ).apply(entityDataInstance, EntityData::new));

    @Override
    public Codec<EntityData> codec() {
        return CODEC;
    }

    @Override
    public EntityData fromNetwork(FriendlyByteBuf buffer) {
        try {
            // EntityId
            final String entityId = buffer.readUtf();
            // Envs
            final List<String> validEnvs = new ArrayList<>();
            SerializationHelper.deserializeStringCollection(buffer, validEnvs);
            // Total grow ticks
            final int growTicks = buffer.readInt();
            // If the cage requires water
            final boolean requiresWater = buffer.readBoolean();
            // Loot data
            final List<LootData> results = new ArrayList<>();
            final int length = buffer.readInt();
            for (int i = 0; i < length; i++) {
                results.add(LootData.deserializeBuffer(buffer));
            }
            // Sampler tier
            final int tier = buffer.readInt();
            // Create entity data
            return new EntityData(entityId, validEnvs, growTicks, requiresWater, results, tier);
        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read entityData recipe from network buffer.", e);
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, EntityData recipe) {
        try {
            // EntityId
            buffer.writeUtf(recipe.getEntityId());
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
            throw new IllegalStateException("Failed to write entityData recipe to the network buffer.", e);
        }
    }

}