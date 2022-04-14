package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedItems;
import com.corgam.cagedmobs.setup.Constants;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class CagedMobsPlugin implements IModPlugin {

    public static final RecipeType<MobData> MOB_CAGE =
            RecipeType.create(Constants.MOD_ID, "entity", MobData.class);

    @Override
    public ResourceLocation getPluginUid () {
        return new ResourceLocation(Constants.MOD_ID, "jei");
    }

    @Override
    public void registerRecipeCatalysts (IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(CagedItems.MOB_CAGE.get()), MOB_CAGE);
        registration.addRecipeCatalyst(new ItemStack(CagedItems.HOPPING_MOB_CAGE.get()), MOB_CAGE);
    }

    @Override
    public void registerRecipes (IRecipeRegistration registration) {
        final List<MobData> entities = RecipesHelper.getEntitiesRecipesList(RecipesHelper.getRecipeManager());
        // Subtract the blacklisted entities
        List<EntityType<?>> blacklistedEntities = RecipesHelper.getEntityTypesFromConfigList();
        if(!CagedMobs.SERVER_CONFIG.isEntitiesListInWhitelistMode()) {
            // Remove blacklisted entities
            entities.removeIf(data -> blacklistedEntities.contains(data.getEntityType()));
        }else{
            // Remove all except whitelisted entities
            entities.removeIf(data -> !blacklistedEntities.contains(data.getEntityType()));
        }
        // Create JEI recipes
        registration.addRecipes(MOB_CAGE, entities);
    }

    @Override
    public void registerCategories (IRecipeCategoryRegistration registration) {

        registration.addRecipeCategories(new EntityCategory(registration.getJeiHelpers().getGuiHelper()));
    }

}
