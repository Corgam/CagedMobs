package com.corgam.cagedmobs.serializers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Map;

public class RecipesHelper {

    // Some helper functions

    public static Map<ResourceLocation, IRecipe<?>> getRecipes (IRecipeType<?> recipeType, RecipeManager manager) {
        final Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipesMap = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, "field_199522_d");
        return recipesMap.get(recipeType);
    }

    public static RecipeManager getRecipeManager(){
        return Minecraft.getInstance().player.connection.getRecipeManager();
        //return DistExecutor.safeRunForDist(() ->RecipesHelper::getRecipeManagerClient, () -> RecipesHelper::getRecipeManagerServer);
    }

    @OnlyIn(Dist.CLIENT)
    private static RecipeManager getRecipeManagerClient() {
        return Minecraft.getInstance().player.connection.getRecipeManager();
    }
    @OnlyIn(Dist.DEDICATED_SERVER)
    private static RecipeManager getRecipeManagerServer() {
        return ServerLifecycleHooks.getCurrentServer().getRecipeManager();
    }

}