package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.mob.MobData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.EntitiesManager")
public class EntitiesManager implements IRecipeManager<MobData> {

    public EntitiesManager() {}

    // Used for creating new entityRecipe with just one valid environment
    @ZenCodeType.Method
    public CTEntity create(String id, EntityType<?> entityType, int growTicks, boolean requiresWater, int tier, String environment) {
        return this.create(id,entityType,growTicks, requiresWater, tier, new String[] {environment});
    }

    // Used for creating new entityRecipe with more valid environments
    @ZenCodeType.Method
    public CTEntity create(String id, EntityType<?> entityType, int growTicks, boolean requiresWater, int tier, String[] environments) {
        final CTEntity entity = new CTEntity(id, entityType, growTicks, requiresWater, tier, environments );
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, entity.getMobData(), ""));
        return entity;
    }

    @ZenCodeType.Method
    public CTEntity getEntity(String id){
        ResourceLocation resource = ResourceLocation.tryParse(id);
        if(resource != null) {
            final MobData recipe = this.getRecipes().get(resource);
            if (recipe != null) {
                return new CTEntity(recipe);
            }
        }
        throw new IllegalStateException("CAGEDMOBS: Invalid CraftTweaker Entity recipe ID: " + id);
    }

    @Override
    public ResourceLocation getBracketResourceLocation () {
        return CagedRecipeSerializers.MOB_RECIPE_SERIALIZER.getId();
    }

    @Override
    public RecipeType<MobData> getRecipeType () {
        return CagedRecipeTypes.MOB_RECIPE.get();
    }
}

