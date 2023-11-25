package com.corgam.cagedmobs.serializers.environment;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class EnvironmentDataSerializer implements RecipeSerializer<EnvironmentData>{

    public EnvironmentDataSerializer(){
    }

    // Used to serialize all EnvData recipes from JSON files
    @Override
    public EnvironmentData fromJson(ResourceLocation recipeId, JsonObject json) {
        // Input item
        final Ingredient inputItem = Ingredient.fromJson(json.getAsJsonObject("input"));
        // Block to render
        final BlockState renderState = SerializationHelper.deserializeBlockState(json.getAsJsonPrimitive("render"));
        // Grow modifier
        final float growModifier = GsonHelper.getAsFloat(json, "growModifier");
        // Categories
        final Set<String> categories = new HashSet<>();
        for(final JsonElement e : json.getAsJsonArray("categories")){
            categories.add(e.getAsString().toLowerCase());
        }
        // Error checks
        if (growModifier <= -1) {
            throw new IllegalArgumentException("Environment " + recipeId.toString() + " has an invalid grow modifier. It must be greater than -1.");
        }
        return new EnvironmentData(recipeId, inputItem, renderState, growModifier, categories);
    }

    @Override
    public EnvironmentData fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        try {
            // Input item
            final Ingredient inputItem = Ingredient.fromNetwork(buffer);
            // Block to render
            final BlockState renderState = SerializationHelper.deserializeBlockState(buffer);
            // Grow modifier
            final float growModifier = buffer.readFloat();
            // Categories
            final Set<String> categories = new HashSet<>();
            SerializationHelper.deserializeStringCollection(buffer, categories);

            return new EnvironmentData(recipeId, inputItem, renderState, growModifier, categories);
        }catch(final Exception e){
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to read environmentData with id: " + recipeId.toString() + " from packet buffer.");
        }
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, EnvironmentData recipe) {
        try{
            // Input item
            recipe.getInputItem().toNetwork(buffer);
            // Block to render
            SerializationHelper.serializeBlockState(buffer, recipe.getRenderState());
            // Grow modifier
            buffer.writeFloat(recipe.getGrowModifier());
            // Categories
            SerializationHelper.serializeStringCollection(buffer, recipe.getEnvironments());
        }catch (final Exception e) {
            CagedMobs.LOGGER.catching(e);
            throw new IllegalStateException("Failed to write environmentData with id " + recipe.getId().toString() + " to the packet buffer.");
        }
    }
}
