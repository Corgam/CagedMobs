package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.corgam.cagedmobs.serializers.entity.EntityDataSerializer.deserializeLootData;

public class AdditionalLootDataSerializer implements RecipeSerializer<AdditionalLootData> {

    public static final Codec<AdditionalLootData> CODEC =  RecordCodecBuilder.create((entityDataInstance) -> entityDataInstance.group(
            // EntityID
            ResourceLocation.CODEC.fieldOf("entity").orElse(new ResourceLocation("pig")).forGetter(AdditionalLootData::getEntityID),
            // Loot
            Codec.list(LootData.CODEC).fieldOf("results").orElse(new ArrayList<>()).forGetter(AdditionalLootData::getResults),
            // Remove from entity
            Codec.BOOL.fieldOf("removeFromEntity").orElse(false).forGetter(AdditionalLootData::isRemoveFromEntity)
    ).apply(entityDataInstance, AdditionalLootData::new));

    @Override
    public Codec<AdditionalLootData> codec() {
        return AdditionalLootDataSerializer.CODEC;
    }

    @Override
    public @Nullable AdditionalLootData fromNetwork(FriendlyByteBuf pBuffer) {
        try {
            // EntityID
            final ResourceLocation entityID = pBuffer.readResourceLocation();
            // Loot data
            final List<LootData> results = new ArrayList<>();
            final int length = pBuffer.readInt();
            for (int i = 0; i < length; i++) {
                results.add(LootData.deserializeBuffer(pBuffer));
            }
            // Remove from entity
            final boolean removeFromEntity = pBuffer.readBoolean();
            // Return final object
            return new AdditionalLootData(entityID, results, removeFromEntity);
        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read additionalLootData from network buffer.");
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, AdditionalLootData recipe) {
        try {
            // Entity
            buffer.writeResourceLocation(recipe.getEntityID());
            // Loot data
            buffer.writeInt(recipe.getResults().size());
            for( final LootData data : recipe.getResults()){
                LootData.serializeBuffer(buffer, data);
            }
            // Remove from entity
            buffer.writeBoolean(recipe.isRemoveFromEntity());
        }catch (final Exception e) {
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to write additionalLootData with id: " + recipe.getEntityID().toString() + " to the packet buffer.");
        }
    }
}
