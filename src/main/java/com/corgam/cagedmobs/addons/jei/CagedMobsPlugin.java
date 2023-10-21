package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.registers.CagedItems;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class CagedMobsPlugin implements IModPlugin {

    public static final RecipeType<EntityDataWrapper> ENTITY_RECIPE = RecipeType.create(CagedMobs.MOD_ID, "entity", EntityDataWrapper.class);

    @Override
    public ResourceLocation getPluginUid () {
        return new ResourceLocation(CagedMobs.MOD_ID, "jei");
    }


    @Override
    public void registerRecipeCatalysts (IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(CagedItems.MOB_CAGE.get()), ENTITY_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(CagedItems.HOPPING_MOB_CAGE.get()), ENTITY_RECIPE);
    }

    @Override
    public void registerRecipes (IRecipeRegistration registration) {
        final RecipeManager recipeManager = RecipesHelper.getRecipeManager();
        final List<EntityData> entities = new ArrayList<>(RecipesHelper.getEntitiesRecipesList(recipeManager));
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
        registration.addRecipes(ENTITY_RECIPE, entities.stream().map(EntityDataWrapper::new).collect(Collectors.toList()));
    }

    @Override
    public void registerCategories (IRecipeCategoryRegistration registration) {
        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new EntityDataCategory(guiHelper, ENTITY_RECIPE));
    }

    // Subtype interpreter for samplers

    private static class DnaSamplerSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
        @Override
        public String apply(ItemStack ingredient, UidContext context) {
            if (ingredient.getItem() instanceof DnaSamplerItem sampler) {
                EntityType<?> entityType = sampler.getEntityType(ingredient);
                if (entityType != null) {
                    return sampler.getEntityType(ingredient).toString();
                }
            }
            return this.NONE;
        }
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        DnaSamplerSubtypeInterpreter interpreter = new DnaSamplerSubtypeInterpreter();
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CagedItems.NETHERITE_DNA_SAMPLER.get(), interpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CagedItems.DIAMOND_DNA_SAMPLER.get(), interpreter);
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, CagedItems.DNA_SAMPLER.get(), interpreter);
    }

}