package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import com.corgam.cagedmobs.helpers.EntityRendererHelper;
import com.corgam.cagedmobs.registers.CagedItems;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.LootData;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EntityDataWrapper implements IRecipeCategoryExtension {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private final EntityData entityData;
    private final List<ItemStack> envs = NonNullList.create();
    private final List<LootData> drops = NonNullList.create();
    private final List<ItemStack> samplers = NonNullList.create();
    private final List<Integer> cookedIDs = new ArrayList<>();
    private final boolean requiresWater;
    private final int ticks;

    public static float rotation = 0.0f;
    private static double yaw = 0;

    public EntityDataWrapper(EntityData entityData){
        this.entityData = entityData;
        // Read valid envs based on entity
        for(EnvironmentData env : RecipesHelper.getEnvsRecipesList(RecipesHelper.getRecipeManager())){
            if(RecipesHelper.isEnvValidForEntity(entityData,env)){
                this.envs.addAll(Arrays.asList(env.getInputItem().getItems()));
            }
        }
        // Add DNA samplers to the recipe based on the tier of recipe
        if(entityData.getSamplerTier() >= 3){
            this.samplers.add(new ItemStack(CagedItems.NETHERITE_DNA_SAMPLER.get()));
        }else if(entityData.getSamplerTier() == 2){
            this.samplers.add(new ItemStack(CagedItems.NETHERITE_DNA_SAMPLER.get()));
            this.samplers.add(new ItemStack(CagedItems.DIAMOND_DNA_SAMPLER.get()));
        }else{
            this.samplers.add(new ItemStack(CagedItems.NETHERITE_DNA_SAMPLER.get()));
            this.samplers.add(new ItemStack(CagedItems.DIAMOND_DNA_SAMPLER.get()));
            this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER.get()));
        }
        // Add loot
        int lootIndex = 0;
        List<Item> blacklistedItems = RecipesHelper.getItemsFromConfigList();
        for(LootData data : entityData.getResults()){
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
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE.get(), RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof AdditionalLootData additionalLootData) {
                // Check for null exceptions
                if(additionalLootData.getEntityType() != null && this.entityData.getEntityType() != null){
                    // Check if the same entity type
                    if(this.entityData.getEntityType().equals(additionalLootData.getEntityType())) {
                        // For each loot data
                        for(LootData data : additionalLootData.getResults()){
                            // Add loot
                            if(!additionalLootData.isRemoveFromEntity()){
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
                            // Remove loot
                            }else{
                                this.drops.removeIf(drop -> drop.getItem().getItem().equals(data.getItem().getItem()));
                            }
                        }
                    }
                }
            }
        }
        // Set up required ticks
        this.ticks = entityData.getTotalGrowTicks();
        // Set up if the recipe requires water
        this.requiresWater = entityData.ifRequiresWater();
    }

    public List<LootData> getDrops() {
        return this.drops;
    }

    public List<ItemStack> getEnvsItems() {
        return this.envs;
    }

    public EntityData getEntityData() {
        return entityData;
    }

    public List<ItemStack> getSamplers() {
        return this.samplers;
    }

    public List<Integer> getCookedIDs() {
        return this.cookedIDs;
    }

    public int getTicks(){
        return this.ticks;
    }

    public int getSeconds(){
        return this.ticks/20;
    }

    public boolean ifRequiresWater(){
        return this.requiresWater;
    }

    public void setRecipe(IRecipeLayoutBuilder builder) {
        // Add samplers without NBT for easier search
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(this.getSamplers());

        // Add DNA Samplers with the specific NBT data
        final IRecipeSlotBuilder samplersSlot = builder.addSlot(RecipeIngredientRole.INPUT, 15, 62+20);
        samplersSlot.addItemStacks(this.getSampledSamplers()).setSlotName("samplers");
        if(!CagedMobs.SERVER_CONFIG.areSpawnEggsDisabled()){
            SpawnEggItem spawnEgg = ForgeSpawnEggItem.fromEntityType(entityData.getEntityType());
            if(spawnEgg != null){
                samplersSlot.addItemStack(spawnEgg.getDefaultInstance());
            }
        }

        // Soil Inputs
        final IRecipeSlotBuilder environmentsSlot = builder.addSlot(RecipeIngredientRole.INPUT, 15 + 20, 62+20);
        environmentsSlot.addItemStacks(this.getEnvsItems()).setSlotName("environments");
        environmentsSlot.addTooltipCallback(this.getEnvTooltip());

        int nextSlotId = 2;
        List<Item> blacklistedItems = RecipesHelper.getItemsFromConfigList();
        for (final LootData entry : this.getDrops()) {
            // If items not blacklisted draw them
            if(!CagedMobs.SERVER_CONFIG.isItemsListInWhitelistMode()){
                if(!blacklistedItems.contains(entry.getItem().getItem())){
                    int relativeSlotId = nextSlotId - 2;
                    final IRecipeSlotBuilder lootSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 101 + 19 * (relativeSlotId % 4), 6 + 19 * (relativeSlotId / 4));
                    if(entry.isCooking() && this.getCookedIDs().contains(relativeSlotId)){
                        lootSlot.addItemStack(entry.getCookedItem());
                    }else{
                        lootSlot.addItemStack(entry.getItem());
                    }
                    nextSlotId++;
                    // Add tooltip
                    lootSlot.addTooltipCallback(this.getLootTooltip(entry));
                }
            }else{
                if(blacklistedItems.contains(entry.getItem().getItem())){
                    int relativeSlotId = nextSlotId - 2;
                    final IRecipeSlotBuilder lootSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 100 + 19 * (relativeSlotId % 4), 5 + 19 * (relativeSlotId / 4));
                    if(entry.isCooking() && this.getCookedIDs().contains(relativeSlotId)){
                        lootSlot.addItemStack(entry.getCookedItem());
                    }else{
                        lootSlot.addItemStack(entry.getItem());
                    }
                    nextSlotId++;
                    // Add tooltip
                    lootSlot.addTooltipCallback(this.getLootTooltip(entry));
                }
            }
        }
    }

    public void draw(GuiGraphics pGuiGraphics, IGuiHelper guiHelper) {
        // Draw Seed & Soil
        guiHelper.getSlotDrawable().draw(pGuiGraphics, 14, 62+19);
        guiHelper.getSlotDrawable().draw(pGuiGraphics, 14+20, 62 + 19);
        // Draw Drops
        for (int nextSlotId = 2; nextSlotId < 22; nextSlotId++) {
            final int relativeSlotId = nextSlotId - 2;
            guiHelper.getSlotDrawable().draw(pGuiGraphics, 100 + 19 * (relativeSlotId % 4), 5 + 19 * (relativeSlotId / 4));
        }
        // Create the entity object to render
        Level level = Minecraft.getInstance().level;
        Optional<Entity> entity = EntityRendererHelper.createEntity(level, this.getEntityData().getEntityType());
        // Render the entity if created correctly
        if(entity.isPresent()){
            rotation = (rotation+ 0.5f)% 360;
            EntityRendererHelper.renderEntity(pGuiGraphics, 33, 120, 38 - yaw, 70, rotation, entity.get() );
            // Update yaw
            yaw = (yaw + 1.5) % 720.0F;
        }
        // Draw entity name
        if(this.getEntityData() != null && this.getEntityData().getEntityType() != null) {
            pGuiGraphics.drawString(Minecraft.getInstance().font, this.getEntityData().getEntityType().getDescription(), 5, 2, 8, false);
        }
        // Draw required ticks
        pGuiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.tooltip.cagedmobs.entity.ticks", this.getSeconds()), 10, 102, 8, false);
        // Draw waterlogged info if it requires water
        if(this.ifRequiresWater()){
            pGuiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.tooltip.cagedmobs.entity.requiresWater", this.getSeconds()).withStyle(ChatFormatting.BLUE), 5, 112, 8, false);
        }
    }

    public List<ItemStack> getSampledSamplers() {
        List<ItemStack> ret = NonNullList.create();
        for (ItemStack stack : samplers) {
            stack = stack.copy();
            EntityType<?> type = entityData.getEntityType();
            CompoundTag nbt = new CompoundTag();
            SerializationHelper.serializeEntityTypeNBT(nbt, type);
            stack.setTag(nbt);

            ret.add(stack);
        }
        return ret;
    }

    private IRecipeSlotTooltipCallback getEnvTooltip() {
        return (view, tooltip) -> {
            if(view.getDisplayedItemStack().isPresent()){
                ItemStack displayedItem = view.getDisplayedItemStack().get();
                EnvironmentData env = MobCageBlockEntity.getEnvironmentDataFromItemStack(displayedItem);
                if(env != null){
                    tooltip.add(Component.translatable("jei.tooltip.cagedmobs.entity.growModifier",  DECIMAL_FORMAT.format(env.getGrowModifier() * 100 - 100)));
                }
            }
        };
    }

    private IRecipeSlotTooltipCallback getLootTooltip(LootData entry) {
        return (view, tooltip) -> {
            if (view.getDisplayedItemStack().isPresent()){
                ItemStack displayedItem = view.getDisplayedItemStack().get();
                tooltip.add(Component.translatable("jei.tooltip.cagedmobs.entity.chance",  DECIMAL_FORMAT.format(entry.getChance() * 100)));
                if(entry.getMinAmount() == entry.getMaxAmount()){
                    tooltip.add(Component.translatable("jei.tooltip.cagedmobs.entity.amountEqual",entry.getMinAmount()));
                }else{
                    tooltip.add(Component.translatable("jei.tooltip.cagedmobs.entity.amount",entry.getMinAmount(), entry.getMaxAmount()));
                }
                if(entry.isLighting()){
                    tooltip.add(Component.translatable("jei.tooltip.cagedmobs.entity.lightning_upgrade").withStyle(ChatFormatting.YELLOW));
                }
                if(entry.isCooking() && displayedItem.getItem().equals(entry.getCookedItem().getItem())){
                    tooltip.add(Component.translatable("jei.tooltip.cagedmobs.entity.cooking_upgrade").withStyle(ChatFormatting.YELLOW));
                }
                if(entry.isArrow()){
                    tooltip.add(Component.translatable("jei.tooltip.cagedmobs.entity.arrow_upgrade").withStyle(ChatFormatting.YELLOW));
                }
                if(entry.hasColor()){
                    tooltip.add(Component.translatable("jei.tooltip.cagedmobs.entity.colorItem").withStyle(ChatFormatting.YELLOW));
                }
            }
        };
    }
}

