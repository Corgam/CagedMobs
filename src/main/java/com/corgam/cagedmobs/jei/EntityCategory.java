package com.corgam.cagedmobs.jei;

import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.setup.CagedItems;
import com.corgam.cagedmobs.setup.Constants;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityCategory implements IRecipeCategory<EntityWrapper> {

    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "entity");

    private static final BackgroundDrawable ENTITY_BG_DRAWABLE = new BackgroundDrawable("textures/gui/entity.png", 166, 93);

    private final IDrawable icon;
    private final IDrawableStatic slotDrawable;

    public EntityCategory(IGuiHelper gui){
        this.icon = gui.createDrawableIngredient(new ItemStack(CagedItems.MOB_CAGE.get()));
        this.slotDrawable = gui.getSlotDrawable();
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends EntityWrapper> getRecipeClass() {
        return EntityWrapper.class;
    }

    @Override
    public String getTitle() {
        String name = new TranslationTextComponent("jei.category.cagedmobs.entity").getString();
        return name;
    }

    @Override
    public IDrawable getBackground() {
        return ENTITY_BG_DRAWABLE;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(EntityWrapper entityWrapper, IIngredients iIngredients) {
        entityWrapper.setIngredients(iIngredients);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, EntityWrapper entityWrapper, IIngredients iIngredients) {
        final IGuiItemStackGroup stacks = iRecipeLayout.getItemStacks();

        // Entity spawn egg
        stacks.init(0, true, 0, 19 * 0);
        stacks.set(0, entityWrapper.getSamplers());

        // Soil Inputs
        stacks.init(1, true, 32, 45 + 19 * 1);
        stacks.set(1, entityWrapper.getEnvsItems());

        int nextSlotId = 2;

        for (final LootData entry : entityWrapper.getDrops()) {

            int relativeSlotId = nextSlotId - 2;
            stacks.init(nextSlotId, false, 100 + 19 * (relativeSlotId % 4), 15 + 19 * (relativeSlotId / 4));
            if(entry.isCooking() && entityWrapper.getCookedIDs().contains(relativeSlotId)){
                stacks.set(nextSlotId, entry.getCookedItem());
            }else{
                stacks.set(nextSlotId, entry.getItem());
            }
            nextSlotId++;
        }

        stacks.addTooltipCallback(entityWrapper::getTooltip);
    }

    @Override
    public void draw (EntityWrapper recipe, MatrixStack matrix, double mouseX, double mouseY) {
        // Draw Seed & Soil
        this.slotDrawable.draw(matrix, 0, 19 * 0);
        this.slotDrawable.draw(matrix, 32, 45 + 19 * 1);
        // Draw Drops
        for (int nextSlotId = 2; nextSlotId < 18; nextSlotId++) {

            final int relativeSlotId = nextSlotId - 2;
            this.slotDrawable.draw(matrix, 100 + 19 * (relativeSlotId % 4), 15 + 19 * (relativeSlotId / 4));
        }
        // Draw entity
        recipe.drawInfo(getBackground().getWidth(), getBackground().getHeight(), matrix, mouseX, mouseY);
        // Draw entity name
        matrix.push();
        matrix.translate(5, 2, 0);
        Minecraft.getInstance().fontRenderer.func_238421_b_(matrix, new TranslationTextComponent(recipe.getEntity().getEntityType().getTranslationKey()).getString(), 0, 0, 8);
        matrix.pop();
    }
}
