package com.corgam.cagedmobs.serializers.environment;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class EnvironmentData implements Recipe<Inventory> {

    private final ResourceLocation id;
    private Ingredient inputItem;
    private BlockState renderState;
    private float growModifier;
    private final Set<String> environments;

    public EnvironmentData(ResourceLocation id, Ingredient item, BlockState renderState, float growModifier, Set<String> categories){
        this.id = id;
        this.inputItem = item;
        this.renderState = renderState;
        this.growModifier = growModifier;
        this.environments = categories;
        // Add the id to the list of loaded recipes
        if(id != null && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded EnvironmentData recipe with id: " + id.toString());
        }
    }

    @Override
    public boolean matches(Inventory inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(Inventory pContainer) {
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
        return CagedRecipeSerializers.ENVIRONMENT_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return CagedRecipeTypes.ENVIRONMENT_RECIPE;
    }

    public Ingredient getInputItem() {
        return inputItem;
    }

    public BlockState getRenderState() {
        return renderState;
    }

    public float getGrowModifier() {
        return growModifier;
    }

    public Set<String> getEnvironments() {
        return environments;
    }

    public void setGrowthModifier(float modifier) {
        this.growModifier = modifier;
    }

    public void setRenderState(BlockState state) {
        this.renderState = state;
    }

    public void setInputItem(Ingredient item) {
        this.inputItem = item;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
