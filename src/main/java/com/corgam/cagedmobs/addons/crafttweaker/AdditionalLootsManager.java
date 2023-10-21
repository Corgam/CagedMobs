package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.entity.CTEntityIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.AdditionalLootsManager")
public class AdditionalLootsManager implements IRecipeManager<AdditionalLootData> {

    public AdditionalLootsManager() {}

    // Used for creating new additionalLootRecipe
    @ZenCodeType.Method
    public CTAdditionalLoot create(String id, EntityType<?> entityType, Boolean removeFromEntity) {
        final CTAdditionalLoot additionalLoot = new CTAdditionalLoot(id, entityType, removeFromEntity);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, additionalLoot.getAdditionalLootData(), ""));
        return additionalLoot;
    }

    @ZenCodeType.Method
    public CTAdditionalLoot getAdditionalLoot(String id){
        ResourceLocation resource = ResourceLocation.tryParse(id);
        if(resource != null){
            final AdditionalLootData recipe = this.getRecipes().get(resource);
            if (recipe != null) {
                return new CTAdditionalLoot(recipe);
            }
        }
        throw new IllegalStateException("CAGEDMOBS: Invalid CraftTweaker additionalLootRecipe ID: " + id);
    }

    @Override
    public ResourceLocation getBracketResourceLocation () {
        return CagedRecipeSerializers.ADDITIONAL_LOOT_RECIPE_SERIALIZER.getId();
    }

    @Override
    public RecipeType<AdditionalLootData> getRecipeType () {
        return CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE.get();
    }
}