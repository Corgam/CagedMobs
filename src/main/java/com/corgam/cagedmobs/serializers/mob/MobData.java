package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.setup.CagedItems;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.*;

public class MobData implements IRecipe<IInventory> {

    private final ResourceLocation id;
    private final String entityTypeString;
    private EntityType<?> entityType;
    private final Set<String> environments;
    private int growTicks;
    private boolean requiresWater;
    private final List<LootData> results;
    private int samplerTier;

    public MobData(ResourceLocation id, EntityType<?> entityType, Set<String> environments, int growTicks, boolean requiresWater, List<LootData> results, int tier){
        this.id = id;
        this.environments = environments;
        this.entityType = entityType;
        this.entityTypeString = entityType.toString();
        this.growTicks = growTicks;
        this.requiresWater = requiresWater;
        this.results = results;
        this.samplerTier = tier;
        if(id != null && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded MobData recipe with id: " + id.toString());
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
    public ItemStack assemble(IInventory inv) {
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
        return MobDataSerializer.INSTANCE;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipesHelper.MOB_RECIPE;
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
}
