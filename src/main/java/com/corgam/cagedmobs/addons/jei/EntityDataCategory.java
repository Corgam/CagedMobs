package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityDataCategory implements IRecipeCategory<EntityDataWrapper> {

    public static final ResourceLocation ID = new ResourceLocation(CagedMobs.MOD_ID, "entity");

    private static final int BG_PADDING = 5, BG_WIDTH = 166, BG_HEIGHT = 111;
    private final IDrawable icon;
    private final IDrawable background;
    private final IGuiHelper guiHelper;
    private final String title;

    public EntityDataCategory(IGuiHelper gui){
        this.guiHelper = gui;
        this.icon = gui.createDrawableIngredient(new ItemStack(CagedItems.MOB_CAGE.get()));
        ResourceLocation backgroundRL = new ResourceLocation(CagedMobs.MOD_ID, "textures/gui/jei_recipe.png");
        this.background = gui.createDrawable(backgroundRL,0,0,BG_WIDTH+BG_PADDING*2,BG_HEIGHT+BG_PADDING*2);
        this.title = new TranslationTextComponent("jei.category.cagedmobs.entity").getString();
    }


    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(EntityDataWrapper recipe, IIngredients ingredients) {
        recipe.setIngredients(ingredients);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, EntityDataWrapper recipe, IIngredients ingredients) {
        recipe.setRecipe(recipeLayout, recipe, ingredients);
    }

    @Override
    public void draw(EntityDataWrapper recipe, MatrixStack graphics, double mouseX, double mouseY) {
        recipe.draw(graphics, this.guiHelper);
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends EntityDataWrapper> getRecipeClass() {
        return EntityDataWrapper.class;
    }
}