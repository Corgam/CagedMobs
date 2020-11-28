package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.serializers.RecipesHelper;
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

    public static final AdditionalLootDataSerializer SERIALIZER = new AdditionalLootDataSerializer();

    private final ResourceLocation id;
    private EntityType<?> entityType;
    private final List<LootData> results;

    public AdditionalLootData(ResourceLocation id, EntityType<?> entityType, List<LootData> results){
        this.id = id;
        this.entityType = entityType;
        this.results = results;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return AdditionalLootData.SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipesHelper.ADDITIONAL_LOOT_RECIPE;
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
}
