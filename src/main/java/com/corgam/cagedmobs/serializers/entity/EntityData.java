package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.registers.CagedItems;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.*;

public class EntityData implements IRecipe<IInventory> {

    private final ResourceLocation id;
    private EntityType<?> entityType;
    private final Set<String> environments;
    private int growTicks;
    private boolean requiresWater;
    private final List<LootData> results;
    private int samplerTier;

    public EntityData(ResourceLocation id, EntityType<?> entityType, Set<String> environments, int growTicks, boolean requiresWater, List<LootData> results, int tier){
        this.id = id;
        this.environments = environments;
        this.entityType = entityType;
        this.growTicks = growTicks;
        this.requiresWater = requiresWater;
        this.results = results;
        this.samplerTier = tier;
        // Add the id to the list of loaded recipes
        if(id != null && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded EntityData recipe with id: " + id.toString());
        }
    }

    public EntityType<?> getEntityType(){
        // Try to find again entity type if it's null
        if(this.entityType == null){
            Optional<EntityType<?>> entityType = EntityType.byString(this.id.toString());
            if(entityType.isPresent()) {
                this.entityType = entityType.get();
                return entityType.get();
            }
        }
        return this.entityType;
    }

    public void setEntityType(EntityType<?> entityType){
        this.entityType = entityType;
    }

    public Set<String> getValidEnvs(){
        return this.environments;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(IInventory pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return CagedRecipeSerializers.ENTITY_RECIPE_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return CagedRecipeTypes.ENTITY_RECIPE;
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
