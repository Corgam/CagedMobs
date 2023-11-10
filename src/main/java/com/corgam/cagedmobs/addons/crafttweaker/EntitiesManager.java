package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.EntitiesManager")
public class EntitiesManager implements IRecipeManager {

    public EntitiesManager() {}

    // Used for creating new entityRecipe with just one valid environment
    @ZenCodeType.Method
    public CTEntity create(String id, MCEntityType entityType, int growTicks, boolean requiresWater, int tier, String environment) {
        return this.create(id,entityType,growTicks, requiresWater, tier, new String[] {environment});
    }

    // Used for creating new entityRecipe with more valid environments
    @ZenCodeType.Method
    public CTEntity create(String id, MCEntityType entityType, int growTicks, boolean requiresWater, int tier, String[] environments) {
        final CTEntity entity = new CTEntity(id, entityType, growTicks, requiresWater, tier, environments );
        CraftTweakerAPI.apply(new ActionAddRecipe(this, entity.getMobData(), ""));
        return entity;
    }

    @ZenCodeType.Method
    public CTEntity getEntity(String id){
        ResourceLocation resource = ResourceLocation.tryParse(id);
        if(resource != null) {
            final EntityData recipe = (EntityData) this.getRecipes().get(resource);
            if (recipe != null) {
                return new CTEntity(recipe);
            }
        }
        throw new IllegalStateException("CAGEDMOBS: Invalid CraftTweaker Entity recipe ID: " + id);
    }

    @Override
    public ResourceLocation getBracketResourceLocation () {
        return CagedRecipeSerializers.ENTITY_RECIPE_SERIALIZER.getId();
    }

    @Override
    public IRecipeType<EntityData> getRecipeType () {
        return CagedRecipeTypes.ENTITY_RECIPE;
    }
}

