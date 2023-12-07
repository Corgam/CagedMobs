package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.EnvironmentsManager")
public class EnvironmentsManager implements IRecipeManager<EnvironmentData> {

    public EnvironmentsManager() {}

    @ZenCodeType.Method
    public CTEnvironment create (IIngredient item, Block renderBlock, float growModifier, String category) {
        return this.create(item, renderBlock, growModifier, new String[]{category});
    }

    @ZenCodeType.Method
    public CTEnvironment create (IIngredient item, Block renderBlock, float growModifier, String[] categories) {
        final CTEnvironment env = new CTEnvironment(item, renderBlock, growModifier, categories);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, env.getEnvironmentData(), ""));
        return env;
    }

    @ZenCodeType.Method
    public CTEnvironment getEnvironment(String id) {
        ResourceLocation resource = ResourceLocation.tryParse(id);
        if(resource != null){
            final EnvironmentData recipe = this.getRecipes().get(resource).value();
            return new CTEnvironment(recipe);
        }
        throw new IllegalStateException("CAGEDMOBS: Invalid CraftTweaker Environment recipe ID: " + id);
    }

    @Override
    public ResourceLocation getBracketResourceLocation () {
        return CagedRecipeSerializers.ENVIRONMENT_RECIPE_SERIALIZER.getId();
    }

    @Override
    public RecipeType<EnvironmentData> getRecipeType () {
        return CagedRecipeTypes.ENVIRONMENT_RECIPE.get();
    }
}