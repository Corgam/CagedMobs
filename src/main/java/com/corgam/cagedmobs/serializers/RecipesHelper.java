package com.corgam.cagedmobs.serializers;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.stream.Collectors;

public class RecipesHelper {


    public static List<EntityData> getEntitiesRecipesList (RecipeManager manager) {
        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.ENTITY_RECIPE.get()).stream()
                    .map(RecipeHolder::value)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static List<EnvironmentData> getEnvironmentRecipesList(RecipeManager manager) {
        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.ENVIRONMENT_RECIPE.get()).stream()
                    .map(RecipeHolder::value)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static List<AdditionalLootData> getAdditionalLootRecipesList (RecipeManager manager) {
        if (manager != null) {
            return manager.getAllRecipesFor(CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE.get()).stream()
                    .map(RecipeHolder::value)
                    .collect(Collectors.toList());
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
        for(String s : entity.getEnvironments()){
            for(String s2 : env.getCategories()){
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