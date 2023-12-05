package com.corgam.cagedmobs.serializers.environment;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentDataSerializer implements RecipeSerializer<EnvironmentData>{

    private final Codec<EnvironmentData> CODEC = RecordCodecBuilder.create((codecInstance) -> codecInstance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(EnvironmentData::getInputItem),
            ForgeRegistries.BLOCKS.getCodec().fieldOf("render").forGetter(EnvironmentData::getRenderBlock),
            Codec.FLOAT.fieldOf("growModifier").forGetter(EnvironmentData::getGrowModifier),
            Codec.list(Codec.STRING).fieldOf("categories").forGetter(EnvironmentData::getCategories))
            .apply(codecInstance, EnvironmentData::new));

    @Override
    public Codec<EnvironmentData> codec() {
        return this.CODEC;
    }

    @Override
    public EnvironmentData fromNetwork(FriendlyByteBuf buffer) {
        try {
            // Input item
            final Ingredient inputItem = Ingredient.fromNetwork(buffer);
            // Block to render
            final Block renderBlock = SerializationHelper.deserializeBlock(buffer);
            // Grow modifier
            final float growModifier = buffer.readFloat();
            // Categories
            final List<String> categories = new ArrayList<>();
            SerializationHelper.deserializeStringCollection(buffer, categories);
            // Create the environment data
            return new EnvironmentData(inputItem, renderBlock, growModifier, categories);
        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read environmentData recipe from packet buffer.");
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, EnvironmentData recipe) {
        try{
            // Input item
            recipe.getInputItem().toNetwork(buffer);
            // Block to render
            SerializationHelper.serializeBlock(buffer, recipe.getRenderBlock());
            // Grow modifier
            buffer.writeFloat(recipe.getGrowModifier());
            // Categories
            SerializationHelper.serializeStringCollection(buffer, recipe.getCategories());
        }catch (final Exception e) {
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to write environmentData recipe to the packet buffer.");
        }
    }
}
