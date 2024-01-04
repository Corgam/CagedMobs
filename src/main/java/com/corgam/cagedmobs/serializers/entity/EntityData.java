package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedItems;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityData implements Recipe<Inventory> {

    private String entityId;
    private EntityType<?> entityType;
    private ArrayList<String> environments;
    private int growTicks;
    private boolean requiresWater;
    private ArrayList<LootData> results;
    private int samplerTier;

    public EntityData(String entityId, List<String> environments, int growTicks, boolean requiresWater, List<LootData> results, int tier){
        this.entityId = entityId;
        this.environments = new ArrayList<>(environments);
        this.entityType = this.getEntityType();
        this.growTicks = growTicks;
        this.requiresWater = requiresWater;
        this.results = new ArrayList<>(results);
        this.samplerTier = tier;
        // Add the id to the list of loaded recipes
        if(!entityId.isEmpty() && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded EntityData recipe for entity: " + entityId);
        }
    }

    public EntityType<?> getEntityType(){
        // Try to find again entity type if it's null
        if(this.entityType == null){
            Optional<EntityType<?>> entityType = EntityType.byString(this.entityId);
            if(entityType.isPresent()) {
                this.entityType = entityType.get();
                return entityType.get();
            }
        }
        return this.entityType;
    }

    public List<String> getEnvironments(){
        return this.environments;
    }

    public void setEnvironments(ArrayList<String> environments){
        this.environments = environments;
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

    public String getEntityId() {
        return this.entityId;
    }

    public void setEntityId(String entityId){
        this.entityId = entityId;
        // Change entity type
        Optional<EntityType<?>> entityType = EntityType.byString(this.entityId);
        entityType.ifPresent(type -> this.entityType = type);
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

    public ArrayList<LootData> getResults() {
        return this.results;
    }

    public void setResults(ArrayList<LootData> results) {
        this.results = results;
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
