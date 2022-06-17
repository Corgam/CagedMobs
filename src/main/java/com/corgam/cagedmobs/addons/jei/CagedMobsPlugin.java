package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedItems;
import com.corgam.cagedmobs.setup.Constants;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CagedMobsPlugin implements IModPlugin {

    public static final RecipeType<MobData> MOB_CAGE =
            RecipeType.create(Constants.MOD_ID, "entity", MobData.class);
    public static IRecipeCategory<?> MOB_CAGE_CATEGORY = null;
    public static final List<MobData> MobCageRecipes = new ArrayList<>();

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
        // Subtract the blacklisted entities
        MobCageRecipes.addAll(RecipesHelper.getEntitiesRecipesList(RecipesHelper.getRecipeManager()));
        List<EntityType<?>> blacklistedEntities = RecipesHelper.getEntityTypesFromConfigList();
        if(!CagedMobs.SERVER_CONFIG.isEntitiesListInWhitelistMode()) {
            // Remove blacklisted entities
            MobCageRecipes.removeIf(data -> blacklistedEntities.contains(data.getEntityType()));
        }else{
            // Remove all except whitelisted entities
            MobCageRecipes.removeIf(data -> !blacklistedEntities.contains(data.getEntityType()));
        }
        // Create JEI recipes
        registration.addRecipes(MOB_CAGE, MobCageRecipes);
    }

    @Override
    public void registerCategories (IRecipeCategoryRegistration registration) {
        MOB_CAGE_CATEGORY = new EntityCategory(registration.getJeiHelpers().getGuiHelper());
        registration.addRecipeCategories(MOB_CAGE_CATEGORY);
    }

    private static class DnaSamplerSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
        @Override
        public String apply(ItemStack ingredient, UidContext context) {
            if (ingredient.getItem() instanceof DnaSamplerItem sampler) {
                EntityType<?> entity = sampler.getEntityType(ingredient);
                if (entity != null) {
                    return sampler.getEntityType(ingredient).toString();
                }
            }
            return this.NONE;
        }
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        DnaSamplerSubtypeInterpreter interpreter = new DnaSamplerSubtypeInterpreter();
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CagedItems.DNA_SAMPLER_NETHERITE.get(), interpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CagedItems.DNA_SAMPLER_DIAMOND.get(), interpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CagedItems.DNA_SAMPLER.get(), interpreter);
    }
}
