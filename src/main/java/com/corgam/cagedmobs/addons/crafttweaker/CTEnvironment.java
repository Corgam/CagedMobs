package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.Environment")
public class CTEnvironment {

    private final EnvironmentData data;

    public CTEnvironment(IIngredient item, Block renderBlock, float growModifier, String[] categories) {
        this(new EnvironmentData(item.asVanillaIngredient(), renderBlock, growModifier, new ArrayList<>(Arrays.asList(categories))));
    }

    public CTEnvironment(EnvironmentData envData){
        this.data = envData;
    }

    @ZenCodeType.Method
    public CTEnvironment addCategory(String category) {
        this.data.getCategories().add(category);
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment removeCategory(String category) {
        HashSet<String> categories = new HashSet<>(this.data.getCategories());
        categories.remove(category);
        this.data.setCategories(new ArrayList<>(categories));
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment clearCategories() {
        this.data.getCategories().clear();
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment setInputItem(IIngredient ingredient) {
        this.data.setInputItem(ingredient.asVanillaIngredient());
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment setDisplay(Block state) {
        this.data.setRenderBlock(state);
        return this;
    }

    @ZenCodeType.Method
    public CTEnvironment setGrowthModifier(float modifier) {
        this.data.setGrowthModifier(modifier);
        return this;
    }

    public RecipeHolder<EnvironmentData> getEnvironmentData () {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(this.data.getInputItem().getItems()[0].getItem());
        if(itemId != null){
            return new RecipeHolder<>(itemId.withPrefix("environment_data_"), this.data);
        }else{
            String categories = String.join("_", this.data.getCategories());
            return new RecipeHolder<>(new ResourceLocation("environment_data_" + categories), this.data);
        }
    }
}
