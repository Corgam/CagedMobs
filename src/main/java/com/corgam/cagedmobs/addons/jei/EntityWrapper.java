package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blockEntities.MobCageBlockEntity;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedItems;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityWrapper {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private final MobData entity;
    private final List<ItemStack> envs = NonNullList.create();
    private final List<LootData> drops = NonNullList.create();
    private final List<ItemStack> samplers = NonNullList.create();
    private final List<Integer> cookedIDs = new ArrayList<>();
    private final boolean requiresWater;
    private final int ticks;

    public EntityWrapper(MobData entity){
        this.entity = entity;
        // Read valid envs based on entity
        for(EnvironmentData env : RecipesHelper.getEnvsRecipesList(RecipesHelper.getRecipeManager())){
            if(RecipesHelper.isEnvValidForEntity(entity,env)){
                this.envs.addAll(Arrays.asList(env.getInputItem().getItems()));
            }
        }
        // Add DNA samplers to the recipe based on the tier of recipe
        if(entity.getSamplerTier() >= 3){
            this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER_NETHERITE.get()));
        }else if(entity.getSamplerTier() == 2){
            this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER_NETHERITE.get()));
            this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER_DIAMOND.get()));
        }else{
            this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER_NETHERITE.get()));
            this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER_DIAMOND.get()));
            this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER.get()));
        }
        // Add loot
        int lootIndex = 0;
        List<Item> blacklistedItems = RecipesHelper.getItemsFromConfigList();
        for(LootData data : entity.getResults()){
            if(!CagedMobs.SERVER_CONFIG.isItemsListInWhitelistMode()){
                if(!blacklistedItems.contains(data.getItem().getItem())){
                    if(!this.drops.contains(data)) {
                        this.drops.add(data);
                        lootIndex++;
                        // If it has a cooked variant add one more LootData
                        if (data.isCooking()) {
                            this.drops.add(data);
                            // If added cooked variant keep track of cooked IDs
                            this.cookedIDs.add(lootIndex);
                            lootIndex++;
                        }
                    }
                }
            }else{
                if(blacklistedItems.contains(data.getItem().getItem())){
                    if(!this.drops.contains(data)) {
                        this.drops.add(data);
                        lootIndex++;
                        // If it has a cooked variant add one more LootData
                        if (data.isCooking()) {
                            this.drops.add(data);
                            // If added cooked variant keep track of cooked IDs
                            this.cookedIDs.add(lootIndex);
                            lootIndex++;
                        }
                    }
                }
            }

        }
        // Add additional Loot
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.ADDITIONAL_LOOT_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof AdditionalLootData) {
                final AdditionalLootData additionalLootData = (AdditionalLootData) recipe;
                // Check for null exceptions
                if(additionalLootData.getEntityType() == null){continue;}
                if(this.entity.getEntityType() == null){ continue;}
                if(this.entity.getEntityType().equals(additionalLootData.getEntityType())) {
                    for(LootData data : additionalLootData.getResults()){
                        if(!CagedMobs.SERVER_CONFIG.isItemsListInWhitelistMode()){
                            if(!blacklistedItems.contains(data.getItem().getItem())){
                                if(!this.drops.contains(data)){
                                    this.drops.add(data);
                                    lootIndex++;
                                    // If it has a cooked variant add one more LootData
                                    if(data.isCooking()){
                                        this.drops.add(data);
                                        // If added cooked variant keep track of cooked IDs
                                        this.cookedIDs.add(lootIndex);
                                        lootIndex++;
                                    }
                                }
                            }
                        }else{
                            if(blacklistedItems.contains(data.getItem().getItem())){
                                if(!this.drops.contains(data)){
                                    this.drops.add(data);
                                    lootIndex++;
                                    // If it has a cooked variant add one more LootData
                                    if(data.isCooking()){
                                        this.drops.add(data);
                                        // If added cooked variant keep track of cooked IDs
                                        this.cookedIDs.add(lootIndex);
                                        lootIndex++;
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        // Set up required ticks
        this.ticks = entity.getTotalGrowTicks();
        // Set up if the recipe requires water
        this.requiresWater = entity.ifRequiresWater();
    }

    public List<LootData> getDrops() {
        return this.drops;
    }

    public List<ItemStack> getEnvsItems() {
        return this.envs;
    }

    public List<ItemStack> getSamplers() {
        return this.samplers;
    }

    public List<ItemStack> getSampledSamplers() {
        List<ItemStack> ret = NonNullList.create();
        for (ItemStack stack : samplers) {
            stack = stack.copy();
            EntityType<?> type = entity.getEntityType();
            CompoundTag nbt = new CompoundTag();
            SerializationHelper.serializeEntityTypeNBT(nbt, type);
            stack.setTag(nbt);

            ret.add(stack);
        }
        return ret;
    }

    public List<Integer> getCookedIDs() {
        return this.cookedIDs;
    }

    public int getTicks(){
        return this.ticks;
    }

    public void getEnvTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
        ItemStack ingredient = JEIHelper.getIngredient(recipeSlotView);

        if(!ingredient.isEmpty()){
            EnvironmentData env = MobCageBlockEntity.getEnvironmentFromItemStack(ingredient);
            if(env != null){
                tooltip.add(new TranslatableComponent("jei.tooltip.cagedmobs.entity.growModifier",  DECIMAL_FORMAT.format(env.getGrowModifier() * 100 - 100)));
            }
        }
    }

    public void getLootTooltip(int outputSlotIndex, IRecipeSlotView recipeSlotView, List<Component> tooltip) {
        ItemStack ingredient = JEIHelper.getIngredient(recipeSlotView);

        if(!ingredient.isEmpty()){
            LootData loot = this.drops.get(outputSlotIndex);
            tooltip.add(new TranslatableComponent("jei.tooltip.cagedmobs.entity.chance",  DECIMAL_FORMAT.format(loot.getChance() * 100)));
            if(loot.getMinAmount() == loot.getMaxAmount()){
                tooltip.add(new TranslatableComponent("jei.tooltip.cagedmobs.entity.amountEqual",loot.getMinAmount()));
            }else{
                tooltip.add(new TranslatableComponent("jei.tooltip.cagedmobs.entity.amount",loot.getMinAmount(), loot.getMaxAmount()));
            }
            if(loot.isLighting()){
                tooltip.add(new TranslatableComponent("jei.tooltip.cagedmobs.entity.lightning_upgrade").withStyle(ChatFormatting.YELLOW));
            }
            if(loot.isCooking() && ingredient.getItem().equals(loot.getCookedItem().getItem())){
                tooltip.add(new TranslatableComponent("jei.tooltip.cagedmobs.entity.cooking_upgrade").withStyle(ChatFormatting.YELLOW));
            }
            if(loot.isArrow()){
                tooltip.add(new TranslatableComponent("jei.tooltip.cagedmobs.entity.arrow_upgrade").withStyle(ChatFormatting.YELLOW));
            }
            if(loot.hasColor()){
                tooltip.add(new TranslatableComponent("jei.tooltip.cagedmobs.entity.colorItem").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
