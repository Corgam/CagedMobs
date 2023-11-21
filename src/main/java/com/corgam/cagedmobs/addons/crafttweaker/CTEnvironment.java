package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Arrays;
import java.util.HashSet;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.Environment")
public class CTEnvironment {

    private final EnvironmentData environmentData;

    public CTEnvironment(String id, IIngredient item, BlockState renderState, float growModifier, String[] categories) {
        this(new EnvironmentData(ResourceLocation.tryParse(id),item.asVanillaIngredient(),renderState,growModifier,new HashSet<>(Arrays.asList(categories))));
    }

    public CTEnvironment(EnvironmentData envData){
        this.environmentData = envData;
    }

    @ZenCodeType.Method
    public CTEnvironment addCategory (String category) {

        this.environmentData.getEnvironments().add(category);
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment removeCategory (String category) {

        this.environmentData.getEnvironments().remove(category);
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment clearCategories () {

        this.environmentData.getEnvironments().clear();
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment setInputItem (IIngredient ingredient) {

        this.environmentData.setInputItem(ingredient.asVanillaIngredient());
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment setDisplay (BlockState state) {

        this.environmentData.setRenderState(state);
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment setGrowthModifier (float modifier) {
        this.environmentData.setGrowthModifier(modifier);
        return this;
    }

    public EnvironmentData getEnvironmentData(){
        return this.environmentData;
    }
}
