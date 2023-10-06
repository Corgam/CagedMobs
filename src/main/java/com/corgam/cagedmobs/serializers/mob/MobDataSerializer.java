package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.*;

public class MobDataSerializer implements RecipeSerializer<MobData> {

    public MobDataSerializer(){
    }

    // Used to serialize all MobData recipes from JSON files
    @Override
    public MobData fromJson(ResourceLocation id, JsonObject json) {
        // Entity
        final EntityType<?> entityType = SerializationHelper.deserializeEntityType(id, json);
        // Envs
        final Set<String> validEnvs = deserializeEnvsData(id, json);
        // Total grow ticks
        final int growTicks = GsonHelper.getAsInt(json, "growTicks");
        // If it requires water
        boolean requiresWater = false;
        if(json.has("requiresWater")) {
            requiresWater = GsonHelper.getAsBoolean(json, "requiresWater");
        }
        // Loot Data
        final List<LootData> results = deserializeLootData(id, json, entityType);
        // Sampler tier
        final int samplerTier = GsonHelper.getAsInt(json, "samplerTier");

        //Error checks
        if (growTicks <= 0){
            throw new IllegalArgumentException("MobDataRecipe with id: " + id.toString() + " has an invalid growth tick rate. It must use a positive integer.");
        }
        if(samplerTier < 1 || samplerTier > 3){
            throw new IllegalArgumentException("MobDataRecipe with id: " + id.toString() + " has an invalid sampler tier. It must use tiers: 1,2 or 3.");
        }

        return new MobData(id, entityType, validEnvs, growTicks, requiresWater, results, samplerTier);
    }

    @Override
    public MobData fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        try {
            // Entity
            final EntityType<?> entityType = SerializationHelper.deserializeEntityType(id, buffer);
            // Envs
            final Set<String> validEnvs = new HashSet<>();
            SerializationHelper.deserializeStringCollection(buffer, validEnvs);
            // Total grow ticks
            final int growTicks = buffer.readInt();
            // If requires water
            final boolean requiresWater = buffer.readBoolean();
            // Loot data
            final List<LootData> results = new ArrayList<>();
            final int length = buffer.readInt();
            for (int i = 0; i < length; i++) {
                results.add(LootData.deserializeBuffer(buffer));
            }
            // Sampler tier
            final int tier = buffer.readInt();

            return new MobData(id, entityType, validEnvs, growTicks, requiresWater, results, tier);

        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read mobData with id: "+ id.toString() + " from packet buffer.", e);
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, MobData recipe) {
        try {
            // Entity
            SerializationHelper.serializeEntityType(buffer, recipe.getEntityType());
            // Envs
            SerializationHelper.serializeStringCollection(buffer, recipe.getValidEnvs());
            // Total  Grow Ticks
            buffer.writeInt(recipe.getTotalGrowTicks());
            // If requires water
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
            throw new IllegalStateException("Failed to write mobData with id: "+ recipe.getId() + " to the packet buffer.",e);
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
    private static List<LootData> deserializeLootData (ResourceLocation ownerId, JsonObject json, EntityType<?> entityType) {
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