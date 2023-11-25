package com.corgam.cagedmobs.serializers.environment;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class EnvironmentData implements Recipe<Inventory> {

    private Ingredient inputItem;
    private Block renderBlock;
    private float growModifier;
    private final List<String> categories;

    public EnvironmentData(Ingredient item, Block renderBlock, float growModifier, List<String> categories){
        this.inputItem = item;
        this.renderBlock = renderBlock;
        this.growModifier = growModifier;
        this.categories = categories;
        // Add the id to the list of loaded recipes
        if(CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded EnvironmentData recipe for input item: " + this.inputItem.getItems()[0].toString());
        }
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
        return CagedRecipeSerializers.ENVIRONMENT_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return CagedRecipeTypes.ENVIRONMENT_RECIPE.get();
    }

    public Ingredient getInputItem() {
        return this.inputItem;
    }

    public Block getRenderBlock(){
        return this.renderBlock;
    }

    public float getGrowModifier() {
        return this.growModifier;
    }

    public List<String> getCategories() {
        return this.categories;
    }

    public void setGrowthModifier(float modifier) {
        this.growModifier = modifier;
    }

    public void setRenderBlock(Block state) {
        this.renderBlock = state;
    }

    public void setInputItem(Ingredient item) {
        this.inputItem = item;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
