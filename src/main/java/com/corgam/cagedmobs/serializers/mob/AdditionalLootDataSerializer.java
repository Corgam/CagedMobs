package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class AdditionalLootDataSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AdditionalLootData>{

    AdditionalLootDataSerializer(){
        this.setRegistryName(new ResourceLocation("cagedmobs","additional_loot_data"));
    }

    @Override
    public AdditionalLootData read(ResourceLocation id, JsonObject json) {
        // Entity
        final EntityType<?> entityType = SerializationHelper.deserializeEntityType(id, json);
        // Loot Data
        final List<LootData> results = deserializeLootData(id, json, entityType);

            return new AdditionalLootData(id, entityType, results);
    }

    @Override
    public AdditionalLootData read(ResourceLocation id, PacketBuffer buffer) {
        try {
            // Entity
            final EntityType<?> entityType = SerializationHelper.deserializeEntityType(id, buffer);
            // Loot data
            final List<LootData> results = new ArrayList<>();
            final int length = buffer.readInt();
            for (int i = 0; i < length; i++) {
                results.add(LootData.deserializeBuffer(buffer));
            }
            return new AdditionalLootData(id, entityType, results);
        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read additionalLootData with id: " + id.toString() + "from packet buffer.");
        }
    }

    @Override
    public void write(PacketBuffer buffer, AdditionalLootData recipe) {
        try {
            // Entity
            SerializationHelper.serializeEntityType(buffer, recipe.getEntityType());
            // Loot data
            buffer.writeInt(recipe.getResults().size());
            for( final LootData data : recipe.getResults()){
                LootData.serializeBuffer(buffer, data);
            }
        }catch (final Exception e) {
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to write additionalLootData with id: " + recipe.getId().toString() + "to the packet buffer.");
        }
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
