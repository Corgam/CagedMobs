package com.corgam.cagedmobs.serializers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class RecipesHelper {

    public static List<EntityData> getEntitiesRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.ENTITY_RECIPE);
        }

        return Collections.emptyList();
    }

    public static List<EnvironmentData> getEnvsRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.ENVIRONMENT_RECIPE);
        }

        return Collections.emptyList();
    }

    public static List<AdditionalLootData> getAdditionalLootRecipesList (RecipeManager manager) {

        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE);
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

    public static boolean isEnvValidForEntity(EntityData entity, EnvironmentData env) {
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