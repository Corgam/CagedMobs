package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.setup.CagedItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.*;

public class MobData implements Recipe<Inventory> {

    private final ResourceLocation id;
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
        this.growTicks = growTicks;
        this.requiresWater = requiresWater;
        this.results = results;
        this.samplerTier = tier;
        // Add the id to the list of loaded recipes
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
    public boolean matches(Inventory inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(Inventory inv) {
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
    public RecipeSerializer<?> getSerializer() {
        return MobDataSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
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

    @Override
    public boolean isSpecial() {
        return true;
    }
}
