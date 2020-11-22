package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.mob.MobData;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.Entities")
public class Entities implements IRecipeManager {

    public Entities() {}

    // Used for creating new entityRecipe with just one valid environment
    @ZenCodeType.Method
    public CTEntity create(String id, MCEntityType entityType, int growTicks, int tier, String environment) {

        return this.create(id,entityType,growTicks,tier, new String[] {environment});
    }

    // Used for creating new entityRecipe with more valid environments
    @ZenCodeType.Method
    public CTEntity create(String id, MCEntityType entityType, int growTicks, int tier, String[] environments) {

        final CTEntity entity = new CTEntity(id, entityType, growTicks, tier, environments );
        CraftTweakerAPI.apply(new ActionAddRecipe(this, entity.getMobData(), ""));
        return entity;
    }

    @ZenCodeType.Method
    public CTEntity getEntity(String id){
        final IRecipe<?> recipe = this.getRecipes().get(ResourceLocation.tryCreate(id));
        if (recipe instanceof MobData) {
            return new CTEntity((MobData) recipe);
        }else{
            throw new IllegalStateException("CAGEDMOBS: Invalid CraftTweaker entityRecipe ID: " + id);
        }
    }

    @Override
    public ResourceLocation getBracketResourceLocation () {
        return MobData.SERIALIZER.getRegistryName();
    }

    @Override
    public IRecipeType<?> getRecipeType () {
        return RecipesHelper.MOB_RECIPE;
    }
}

