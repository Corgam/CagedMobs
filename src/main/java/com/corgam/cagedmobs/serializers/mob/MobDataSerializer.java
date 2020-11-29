package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.*;

public class MobDataSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MobData>{

    MobDataSerializer(){
        this.setRegistryName(new ResourceLocation("cagedmobs","mob_data"));
    }

    // Used to serialize all MobData recipes from JSON files
    @Override
    public MobData read(ResourceLocation id, JsonObject json) {
        // Entity
        final EntityType<?> entityType = SerializationHelper.deserializeEntityType(id, json);
        // Envs
        final Set<String> validEnvs = deserializeEnvsData(id, json);
        // Total grow ticks
        final int growTicks = JSONUtils.getInt(json, "growTicks");
        // Loot Data
        final List<LootData> results = deserializeLootData(id, json, entityType);
        // Sampler tier
        final int samplerTier = JSONUtils.getInt(json, "samplerTier");

        //Error checks
        if (growTicks <= 0){
            throw new IllegalArgumentException("MobDataRecipe with id: " + id + " has an invalid growth tick rate. It must use a positive integer.");
        }
        if(samplerTier < 1 || samplerTier > 3){
            throw new IllegalArgumentException("MobDataRecipe with id: " + id + " has an invalid sampler tier. It must use tiers: 1,2 or 3.");
        }

        return new MobData(id, entityType, validEnvs, growTicks, results, samplerTier);
    }

    @Override
    public MobData read(ResourceLocation id, PacketBuffer buffer) {
        try {
            // Entity
            final EntityType<?> entityType = SerializationHelper.deserializeEntityType(id, buffer);
            // Envs
            final Set<String> validEnvs = new HashSet<>();
            SerializationHelper.deserializeStringCollection(buffer, validEnvs);
            // Total grow ticks
            final int growTicks = buffer.readInt();
            // Loot data
            final List<LootData> results = new ArrayList<>();
            final int length = buffer.readInt();
            for (int i = 0; i < length; i++) {
                results.add(LootData.deserializeBuffer(buffer));
            }
            // Sampler tier
            final int tier = buffer.readInt();

            return new MobData(id, entityType, validEnvs, growTicks, results, tier);

        }catch(final Exception e){
            throw new IllegalStateException("Failed to read mobData from packet buffer.");
        }
    }

    @Override
    public void write(PacketBuffer buffer, MobData recipe) {
        try {
            // Entity
            SerializationHelper.serializeEntityType(buffer, recipe.getEntityType());
            // Envs
            SerializationHelper.serializeStringCollection(buffer, recipe.getValidEnvs());
            // Total  Grow Ticks
            buffer.writeInt(recipe.getTotalGrowTicks());
            // Loot data
            buffer.writeInt(recipe.getResults().size());
            for( final LootData data : recipe.getResults()){
                LootData.serializeBuffer(buffer, data);
            }
            // Sampler Tier
            buffer.writeInt(recipe.getSamplerTier());

        }catch (final Exception e) {
            throw new IllegalStateException("Failed to write mobData to the packet buffer.");
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
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(nbtName,nbtData);
        stack.setTag(nbt);
        return stack;
    }

}