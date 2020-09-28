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
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.function.Function;

public class EntityCategory implements IRecipeCategory<EntityWrapper> {

    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "entity");

    private static final BackgroundDrawable ENTITY_BG_DRAWABLE = new BackgroundDrawable("textures/gui/entity.png", 166, 93);

    private final IDrawable icon;
    private final IDrawableStatic background;
    private final IDrawableStatic slotDrawable;

    public EntityCategory(IGuiHelper gui){
        this.icon = gui.createDrawableIngredient(new ItemStack(CagedItems.MOB_CAGE.get()));
        this.background = gui.createBlankDrawable(155,57);
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

        // Seed & Soil
        this.slotDrawable.draw(matrix, 0, 19 * 0);
        this.slotDrawable.draw(matrix, 32, 45 + 19 * 1);

        for (int nextSlotId = 2; nextSlotId < 18; nextSlotId++) {

            final int relativeSlotId = nextSlotId - 2;
            this.slotDrawable.draw(matrix, 100 + 19 * (relativeSlotId % 4), 15 + 19 * (relativeSlotId / 4));
        }
//        matrix.push();
//        matrix.translate(7, 2, 0);
//
//        Entity entity = getCachedEntity(recipe);
//        if (entity != null) {
//            float maxSize = 0.42F;
//            float maxEntityDimension = Math.max(entity.getWidth(), entity.getHeight());
//            // If entity is bigger then 1.0D, scale it down.
//            if ((double)maxEntityDimension > 1.0D) {
//                maxSize /= maxEntityDimension;
//            }
//            matrix.translate(0.0D, (double)0.1F, 0.0D);
//            //matrix.scale(maxSize, maxSize, maxSize);
//            IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
//            Minecraft.getInstance().getRenderManager().renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrix, renderTypeBuffer, 15728880);
//        }



        //Minecraft.getInstance().fontRenderer.func_238405_a_(matrix, String.valueOf(recipe.getEntity().getEntityType().toString()), 0, 0, 8);
        //matrix.pop();
    }

    private WeightedSpawnerEntity renderedEntity;

    public Entity getCachedEntity(EntityWrapper recipe) {
            if(this.renderedEntity == null){
                CompoundNBT nbt = new CompoundNBT();
                nbt.putString("id", Registry.ENTITY_TYPE.getKey(recipe.getEntity().getEntityType()).toString());
                this.renderedEntity = new WeightedSpawnerEntity(1, nbt);
            }
            Entity e = EntityType.func_220335_a(this.renderedEntity.getNbt(), Minecraft.getInstance().world, Function.identity());

        return e;
    }
}
