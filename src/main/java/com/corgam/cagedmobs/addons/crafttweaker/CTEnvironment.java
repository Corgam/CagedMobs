package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
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
    public CTEnvironment setInputItem (IIngredient inputIngredient) {
        this.environmentData.setInputItem(inputIngredient.asVanillaIngredient());
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment setDisplay (BlockState displayBlockState) {
        this.environmentData.setRenderState(displayBlockState);
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment setGrowthModifier (float growthModifier) {
        this.environmentData.setGrowthModifier(growthModifier);
        return this;
    }

    public EnvironmentData getEnvironmentData(){
        return this.environmentData;
    }
}
