package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public class AdditionalLootData implements IRecipe<IInventory> {

    private final ResourceLocation id;
    private EntityType<?> entityType;
    private final List<LootData> results;
    private boolean removeFromEntity;

    public AdditionalLootData(ResourceLocation id, EntityType<?> entityType, List<LootData> results, boolean removeFromEntity){
        this.id = id;
        this.entityType = entityType;
        this.results = results;
        this.removeFromEntity = removeFromEntity;
        // Add the id to the list of loaded recipes
        if(id != null && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded AdditionalLootData recipe with id: " + id.toString());
        }
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
        return CagedRecipeSerializers.ADDITIONAL_LOOT_RECIPE_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE;
    }

    public List<LootData> getResults () {
        return this.results;
    }

    public EntityType<?> getEntityType(){
        return this.entityType;
    }

    public void setEntityType(EntityType<?> entityType){
        this.entityType = entityType;
    }

    public boolean isRemoveFromEntity(){
        return this.removeFromEntity;
    }

    public void setRemoveFromEntity(boolean removeFromEntity){
        this.removeFromEntity = removeFromEntity;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

}
