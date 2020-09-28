package com.corgam.cagedmobs.serializers;

import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.env.RecipeTypeEnvData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.serializers.mob.RecipeTypeMobData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

public class RecipesHelper {

    // Recipes

    public static final IRecipeType<MobData> MOB_RECIPE = new RecipeTypeMobData();
    public static final IRecipeType<EnvironmentData> ENV_RECIPE = new RecipeTypeEnvData();

    // Some helper functions

    public static Map<ResourceLocation, IRecipe<?>> getRecipes (IRecipeType<?> recipeType, RecipeManager manager) {
        final Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipesMap = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, "field_199522_d");
        return recipesMap.get(recipeType);
    }

    public static List<MobData> getEntitiesRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.func_241447_a_(RecipesHelper.MOB_RECIPE);
        }

        return Collections.emptyList();
    }

    public static List<EnvironmentData> getEnvsRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.func_241447_a_(RecipesHelper.ENV_RECIPE);
        }

        return Collections.emptyList();
    }

    public static RecipeManager getRecipeManager(){
        try{
            if(EffectiveSide.get().isClient()){
                return RecipesHelper.getRecipeManagerClient();
            }else{
                return RecipesHelper.getRecipeManagerServer();
            }
        }catch(final Exception e){
            throw new RuntimeException(e);
        }
    }

    private static RecipeManager getRecipeManagerClient() {
        if(Minecraft.getInstance().player != null){
            return Minecraft.getInstance().player.connection.getRecipeManager();
        }
        return null;
    }

    private static RecipeManager getRecipeManagerServer() {
        return ServerLifecycleHooks.getCurrentServer().getRecipeManager();
    }

    public static boolean isEnvValidForEntity(MobData entity, EnvironmentData env) {
        for(String s : entity.getValidEnvs()){
            for(String s2 : env.getEnvironments()){
                if(s.matches(s2)){
                    return true;
                }
            }
        }
        return false;
    }
}