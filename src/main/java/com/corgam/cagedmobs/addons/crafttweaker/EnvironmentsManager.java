package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.EnvironmentsManager")
public class EnvironmentsManager implements IRecipeManager {

    public EnvironmentsManager() {}

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
            final EnvironmentData recipe = (EnvironmentData) this.getRecipes().get(resource);
            if (recipe != null) {
                return new CTEnvironment(recipe);
            }
        }
        throw new IllegalStateException("CagedMobs: Invalid CraftTweaker Environment recipe ID: " + id);
    }

    @Override
    public ResourceLocation getBracketResourceLocation () {
        return CagedRecipeSerializers.ENVIRONMENT_RECIPE_SERIALIZER.getId();
    }

    @Override
    public IRecipeType<EnvironmentData> getRecipeType () {
        return CagedRecipeTypes.ENVIRONMENT_RECIPE;
    }
}