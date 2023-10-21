package com.corgam.cagedmobs.serializers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

public class RecipesHelper {

    // Some helper functions

    public static Map<ResourceLocation, Recipe<?>> getRecipes (RecipeType<?> recipeType, RecipeManager manager) {
        final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipesMap = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, "f_44007_");
        return recipesMap.get(recipeType);
    }

    public static List<MobData> getEntitiesRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.MOB_RECIPE.get());
        }

        return Collections.emptyList();
    }

    public static List<EnvironmentData> getEnvsRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.ENVIRONMENT_RECIPE.get());
        }

        return Collections.emptyList();
    }

    public static List<AdditionalLootData> getAdditionalLootRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE.get());
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

    public static List<EntityType<?>> getEntityTypesFromConfigList(){
        List<EntityType<?>> blacklisted = new java.util.ArrayList<>(Collections.emptyList());
        List<? extends String> blacklistedEntities = CagedMobs.SERVER_CONFIG.getEntitiesList();
        for(String s : blacklistedEntities){
            Optional<EntityType<?>> entityType = EntityType.byString(s);
            entityType.ifPresent(blacklisted::add);
        }
        return blacklisted;
    }

    public static boolean isEntityTypeBlacklisted(EntityType<?> type){
        List<EntityType<?>> list = getEntityTypesFromConfigList();
        if(CagedMobs.SERVER_CONFIG.isEntitiesListInWhitelistMode()){
            return !list.contains(type);
        }else{
            return list.contains(type);
        }
    }

    public static List<Item> getItemsFromConfigList(){
        List<Item> blacklisted = new java.util.ArrayList<>(Collections.emptyList());
        List<? extends String> blacklistedItems = CagedMobs.SERVER_CONFIG.getItemsList();
        for(String s : blacklistedItems){
            Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
            if(i != Items.AIR || i != null){
                blacklisted.add(i);
            }
        }
        return blacklisted;
    }
}