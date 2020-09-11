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

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RecipesHelper {

    // Recipes

    public static final IRecipeType<MobData> MOB_RECIPE = new RecipeTypeMobData();
    public static final IRecipeType<EnvironmentData> ENV_RECIPE = new RecipeTypeEnvData();

    // Some helper functions

    public static Map<ResourceLocation, IRecipe<?>> getRecipes (IRecipeType<?> recipeType, RecipeManager manager) {
        final Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipesMap = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, manager, "field_199522_d");
        return recipesMap.get(recipeType);
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

    // Serialization
    public static void serializeStringCollection(PacketBuffer buffer, Set<String> categories) {
        //TODO
    }

    public static void deserializeStringCollection(PacketBuffer buffer, Set<String> categories) {
        //TODO
    }

    public static void serializeBlockState(PacketBuffer buffer, BlockState renderState) {
        //TODO
    }

    public static BlockState deserializeBlockState(PacketBuffer buffer) {
        //TODO
        return Blocks.AIR.getDefaultState();
    }
    public static BlockState deserializeBlockState(JsonElement element) {
        //TODO
        return Blocks.AIR.getDefaultState();
    }

    public static EntityType<?> deserializeEntityType(ResourceLocation id, PacketBuffer buffer) {
        //TODO
        return null;
    }

    public static void serializeEntityType(PacketBuffer buffer, EntityType<?> entityType) {
        //TODO
    }

    public static CompoundNBT serializeEntityTypeNBT(CompoundNBT nbt, EntityType<?> entityType) {
        nbt.putString("entity", EntityType.getKey(entityType).toString());
        return nbt;
    }


    public static EntityType<?> deserializeEntityTypeNBT(CompoundNBT nbt) {
        ResourceLocation res = new ResourceLocation(nbt.getString("entity"));
        if(EntityType.byKey(res.getPath()).isPresent()) {
            return EntityType.byKey(res.getPath()).get();
        }else{
            return null;
        }
    }

    public static EntityType<?> deserializeEntityType(ResourceLocation id, JsonObject json) {
        final String entityTypeString= json.getAsJsonPrimitive("entity").getAsString();
        Optional<EntityType<?>> entityType = EntityType.byKey(entityTypeString);
        if(entityType.isPresent()) {
            return entityType.get();
        }else {
            throw new IllegalArgumentException("MobDataRecipe with id: " + id + " has an invalid entity key. No entity with given key exists.");
        }
    }
}