package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.registers.CagedItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class EntityData implements Recipe<Inventory> {

    private final ResourceLocation entityID;
    private final List<String> environments;
    private int growTicks;
    private boolean requiresWater;
    private final List<LootData> results;
    private int samplerTier;

    public EntityData(ResourceLocation entityID, List<String> environments, int growTicks, boolean requiresWater, List<LootData> results, int tier){
        this.environments = environments;
        this.entityID = entityID;
        this.growTicks = growTicks;
        this.requiresWater = requiresWater;
        this.results = results;
        this.samplerTier = tier;
        // Add the id to the list of loaded recipes
        if(CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded EntityData recipe for EntityType: " + this.entityID.toString());
        }
    }

    public @Nullable EntityType<?> getEntityType(){
        Optional<EntityType<?>> entityType = EntityType.byString(this.entityID.toString());
        return entityType.orElse(null);
    }

    public ResourceLocation getEntityID(){
        return this.entityID;
    }

    public List<String> getEnvironments(){
        return this.environments;
    }

    @Override
    public boolean matches(Inventory inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(Inventory pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CagedRecipeSerializers.ENTITY_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return CagedRecipeTypes.ENTITY_RECIPE.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(CagedItems.DNA_SAMPLER.get());
    }

    public int getTotalGrowTicks () {
        return this.growTicks;
    }

    public void setTotalGrowTicks(int newTicks){
        this.growTicks = newTicks;
    }

    public boolean ifRequiresWater(){
        return this.requiresWater;
    }

    public void setIfRequiresWater(boolean requiresWater){
        this.requiresWater = requiresWater;
    }

    public List<LootData> getResults () {
        return this.results;
    }

    public int getSamplerTier() {
        return samplerTier;
    }

    public void setSamplerTier(int tier){
        this.samplerTier = tier;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
