package com.corgam.cagedmobs.serializers.environment;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnvironmentDataSerializer implements RecipeSerializer<EnvironmentData>{

    public static final Codec<EnvironmentData> CODEC = RecordCodecBuilder.create((builder) -> builder
            .group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(EnvironmentData::getInputItem),
                BlockState.CODEC.fieldOf("render").forGetter(EnvironmentData::getRenderState),
                Codec.FLOAT.fieldOf("growModifier").forGetter(EnvironmentData::getGrowModifier),
                Codec.list(Codec.STRING).fieldOf("categories").forGetter(EnvironmentData::getCategories)
            ).apply(builder, EnvironmentData::new));

    @Override
    public @NotNull Codec<EnvironmentData> codec() {
        return EnvironmentDataSerializer.CODEC;
    }

    @Override
    public @Nullable EnvironmentData fromNetwork(@NotNull FriendlyByteBuf pBuffer) {
        try {
            // Input item
            final Ingredient inputItem = Ingredient.fromNetwork(pBuffer);
            // Block to render
            final BlockState renderState = SerializationHelper.deserializeBlockState(pBuffer);
            // Grow modifier
            final float growModifier = pBuffer.readFloat();
            // Categories
            final List<String> categories = new ArrayList<>();
            SerializationHelper.deserializeStringCollection(pBuffer, categories);
            // Return object
            return new EnvironmentData(inputItem, renderState, growModifier, categories);
        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read environmentData from a network buffer.");
        }
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull EnvironmentData recipe) {
        try{
            // Input item
            recipe.getInputItem().toNetwork(buffer);
            // Block to render
            SerializationHelper.serializeBlockState(buffer, recipe.getRenderState());
            // Grow modifier
            buffer.writeFloat(recipe.getGrowModifier());
            // Categories
            SerializationHelper.serializeStringCollection(buffer, recipe.getCategories());
        }catch (final Exception e) {
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to write environmentData recipe to the network buffer.");
        }
    }
}
