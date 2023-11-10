package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.AdditionalLootsManager")
public class AdditionalLootsManager implements IRecipeManager {

    public AdditionalLootsManager() {}

    // Used for creating new additionalLootRecipe
    @ZenCodeType.Method
    public CTAdditionalLoot create(String id, MCEntityType entityType, Boolean removeFromEntity) {
        final CTAdditionalLoot additionalLoot = new CTAdditionalLoot(id, entityType, removeFromEntity);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, additionalLoot.getAdditionalLootData(), ""));
        return additionalLoot;
    }

    @ZenCodeType.Method
    public CTAdditionalLoot getAdditionalLoot(String id){
        ResourceLocation resource = ResourceLocation.tryParse(id);
        if(resource != null){
            final AdditionalLootData recipe = (AdditionalLootData) this.getRecipes().get(resource);
            if (recipe != null) {
                return new CTAdditionalLoot(recipe);
            }
        }
        throw new IllegalStateException("CAGEDMOBS: Invalid CraftTweaker Additional Loot Data recipe ID: " + id);
    }

    @Override
    public ResourceLocation getBracketResourceLocation () {
        return CagedRecipeSerializers.ADDITIONAL_LOOT_RECIPE_SERIALIZER.getId();
    }

    @Override
    public IRecipeType<AdditionalLootData> getRecipeType () {
        return CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE;
    }
}