package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.Environments")
public class Environments implements IRecipeManager {

    public Environments() {}

    @ZenCodeType.Method
    public CTEnvironment create (String id, IIngredient item, BlockState renderState, float growModifier, String category) {
        return this.create(id,item,renderState,growModifier, new String[]{category});
    }

    @ZenCodeType.Method
    public CTEnvironment create (String id, IIngredient item, BlockState renderState, float growModifier, String[] categories) {
        final CTEnvironment env = new CTEnvironment(id, item, renderState, growModifier, categories);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, env.getEnvironmentData(), ""));
        return env;
    }

    @ZenCodeType.Method
    public CTEnvironment getEnvironment(String id) {
        ResourceLocation resource = ResourceLocation.tryParse(id);
        if(resource != null){
            final IRecipe<?> recipe = this.getRecipes().get(resource);
            if (recipe instanceof EnvironmentData) {
                return new CTEnvironment((EnvironmentData) recipe);
            }
        }
        throw new IllegalStateException("CAGEDMOBS: Invalid CraftTweaker environmentRecipe ID: " + id);
    }

    @Override
    public ResourceLocation getBracketResourceLocation () {
        return EnvironmentData.SERIALIZER.getRegistryName();
    }

    @Override
    public IRecipeType<?> getRecipeType () {
        return RecipesHelper.ENV_RECIPE;
    }
}