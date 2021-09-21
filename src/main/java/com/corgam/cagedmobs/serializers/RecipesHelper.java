package com.corgam.cagedmobs.serializers;

import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.env.RecipeTypeEnvData;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.serializers.mob.RecipeAdditionalLoot;
import com.corgam.cagedmobs.serializers.mob.RecipeTypeMobData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import java.util.*;

public class RecipesHelper {

    // Recipes

    public static final RecipeType<MobData> MOB_RECIPE = new RecipeTypeMobData();
    public static final RecipeType<EnvironmentData> ENV_RECIPE = new RecipeTypeEnvData();
    public static final RecipeType<AdditionalLootData> ADDITIONAL_LOOT_RECIPE = new RecipeAdditionalLoot();

    // Some helper functions

    public static Map<ResourceLocation, Recipe<?>> getRecipes (RecipeType<?> recipeType, RecipeManager manager) {
        final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipesMap = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, "field_199522_d");
        return recipesMap.get(recipeType);
    }

    public static List<MobData> getEntitiesRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(RecipesHelper.MOB_RECIPE);
        }

        return Collections.emptyList();
    }

    public static List<EnvironmentData> getEnvsRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(RecipesHelper.ENV_RECIPE);
        }

        return Collections.emptyList();
    }

    public static List<AdditionalLootData> getAdditionalLootRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(RecipesHelper.ADDITIONAL_LOOT_RECIPE);
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