package com.corgam.cagedmobs.addons.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.world.item.ItemStack;

public class JEIHelper {
    public static ItemStack getIngredient(IRecipeSlotView recipeSlotView) {
        return getIngredient(recipeSlotView.getDisplayedIngredient().orElse(null));
    }

    public static ItemStack getIngredient(ITypedIngredient<?> typedIngredient) {
        ItemStack ingredient = ItemStack.EMPTY;
        if (typedIngredient != null) {
            ingredient = (ItemStack) typedIngredient.getIngredient();
        }

        return ingredient;
    }

    public static ItemStack getIngredient(IFocus<?> focus) {
        return getIngredient(focus.getTypedValue());
    }
}
