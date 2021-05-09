package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.CagedMobs;
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

    private final ResourceLocation id;
    private EntityType<?> entityType;
    private final List<LootData> results;

    public static int NUMBER_OF_LOADED_ADDITIONAL_LOOTDATA_RECIPES = 0;
    public static int NUMBER_OF_NULL_ADDITIONAL_LOOTDATA_RECIPES = 0;

    public AdditionalLootData(ResourceLocation id, EntityType<?> entityType, List<LootData> results){
        this.id = id;
        this.entityType = entityType;
        this.results = results;
        // Add the id to the list of loaded recipes
        if(id != null && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded AdditionalLootData recipe with id: " + id.toString());
           // NUMBER_OF_LOADED_ADDITIONAL_LOOTDATA_RECIPES++;
        }else{
            //NUMBER_OF_NULL_ADDITIONAL_LOOTDATA_RECIPES++;
        }
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
        return AdditionalLootDataSerializer.INSTANCE;
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

    @Override
    public boolean isSpecial() {
        return true;
    }

}
