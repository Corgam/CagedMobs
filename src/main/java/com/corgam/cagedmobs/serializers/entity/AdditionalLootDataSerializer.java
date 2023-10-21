package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;

import static com.corgam.cagedmobs.serializers.entity.EntityDataSerializer.deserializeLootData;

public class AdditionalLootDataSerializer implements RecipeSerializer<AdditionalLootData> {

    public AdditionalLootDataSerializer(){
    }

    @Override
    public AdditionalLootData fromJson(ResourceLocation id, JsonObject json) {
        // Entity
        final EntityType<?> entityType = SerializationHelper.deserializeEntityType(id, json);
        // Loot Data
        final List<LootData> results = deserializeLootData(id, json, entityType);
        // Remove from entity
        boolean removeFromEntity = false;
        if(json.has("removeFromEntity")) {
            removeFromEntity = GsonHelper.getAsBoolean(json, "removeFromEntity");
        }
        // Return results
        return new AdditionalLootData(id, entityType, results, removeFromEntity);
    }

    @Override
    public AdditionalLootData fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        try {
            // Entity
            final EntityType<?> entityType = SerializationHelper.deserializeEntityType(id, buffer);
            // Loot data
            final List<LootData> results = new ArrayList<>();
            final int length = buffer.readInt();
            for (int i = 0; i < length; i++) {
                results.add(LootData.deserializeBuffer(buffer));
            }
            // Remove from entity
            final boolean removeFromEntity = buffer.readBoolean();
            // Return final object
            return new AdditionalLootData(id, entityType, results, removeFromEntity);
        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read additionalLootData with id: " + id.toString() + " from packet buffer.");
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, AdditionalLootData recipe) {
        try {
            // Entity
            SerializationHelper.serializeEntityType(buffer, recipe.getEntityType());
            // Loot data
            buffer.writeInt(recipe.getResults().size());
            for( final LootData data : recipe.getResults()){
                LootData.serializeBuffer(buffer, data);
            }
            // Remove from entity
            buffer.writeBoolean(recipe.isRemoveFromEntity());
        }catch (final Exception e) {
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to write additionalLootData with id: " + recipe.getId().toString() + " to the packet buffer.");
        }
    }
}
