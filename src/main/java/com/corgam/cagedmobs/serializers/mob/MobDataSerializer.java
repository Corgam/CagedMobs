package com.corgam.cagedmobs.serializers.mob;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.*;

public class MobDataSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MobData>{

    MobDataSerializer(){
        this.setRegistryName(new ResourceLocation("cagedmobs","mob_data"));
    }

    @Override
    public MobData read(ResourceLocation id, JsonObject json) {
        final EntityType<?> entityType = deserializeEntityType(id, json);
        final Set<String> validEnvs = deserializeEnvsData(id, json);
        final int growTicks = JSONUtils.getInt(json, "growTicks");
        final List<LootData> results = deserializeLootData(id, json);

        if (growTicks <= 0){
            throw new IllegalArgumentException("MobDataRecipe with id: " + id + " has an invalid growth tick rate. It must use a positive integer.");
        }

        return new MobData(id, entityType, validEnvs, growTicks, results);
    }

    @Override
    public MobData read(ResourceLocation id, PacketBuffer buffer) {
        try {
            //final Ingredient entityType = Ingredient.read(buffer);
            final EntityType<?> entityType = null;
            final Set<String> validEnvs = new HashSet<>();
            //PacketUtils.deserializeStringCollection(buffer, validEnvs);
            final int growTicks = buffer.readInt();
            final List<LootData> results = new ArrayList<>();

            //final int length = buffer.readInt();
            //for (int i = 0; i < length; i++) {
            //    results.add(LootData.deserializeBuffer(buffer));
            //}
            return new MobData(id, entityType, validEnvs, growTicks, results);

        }catch(final Exception e){
            throw new IllegalStateException("Failed to read mob data from packet buffer. This is really bad.");
        }
    }

    @Override
    public void write(PacketBuffer buf, MobData recipe) {
        try {
            //recipe.getEntityType().write(buf);
            buf.writeInt(recipe.getTotalGrowTicks());
            //buf.writeInt(LootData.getResults().size());
            //for (LootData entry : MobDataRecipe.getResults()) {

            //LootData.serialize(buf, entry);
            //}

        }catch (final Exception e) {
            throw new IllegalStateException("Failed to write crop to the packet buffer.");
        }

    }
    // Deserializes entity type
    private static EntityType<?> deserializeEntityType(ResourceLocation id, JsonObject json){
        final String entityTypeString= json.getAsJsonPrimitive("entity").getAsString();
        Optional<EntityType<?>> entityType = EntityType.byKey(entityTypeString);
        if(entityType.isPresent()) {
            return entityType.get();
        }else {
            throw new IllegalArgumentException("MobDataRecipe with id: " + id + " has an invalid entity key. No entity with given key exists.");
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
    private static List<LootData> deserializeLootData (ResourceLocation ownerId, JsonObject json) {
        final List<LootData> loots = new ArrayList<>();
        for (final JsonElement elem : json.getAsJsonArray("results")) {
            if (elem.isJsonObject()) {
                final LootData lootData = LootData.deserialize(elem.getAsJsonObject());
                loots.add(lootData);
            }
        }
        return loots;
    }

}