package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import com.corgam.cagedmobs.helpers.EntityRendererHelper;
import com.corgam.cagedmobs.registers.CagedItems;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import com.corgam.cagedmobs.serializers.environment.EnvironmentData;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.LootData;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.text.DecimalFormat;
import java.util.*;

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
        for(final AdditionalLootData recipe : RecipesHelper.getAdditionalLootRecipesList(RecipesHelper.getRecipeManager())) {
            if(recipe != null) {
                // Check for null exceptions
                if(recipe.getEntityType() != null && this.entityData.getEntityType() != null){
                    // Check if the same entity type
                    if(this.entityData.getEntityType().equals(recipe.getEntityType())) {
                        // For each loot data
                        for(LootData data : recipe.getResults()){
                            // Add loot
                            if(!recipe.isRemoveFromEntity()){
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

    private List<ItemStack> getAllInputItemStacks(boolean includeHidden){
        List<ItemStack> inputs = new ArrayList<>();
        if(includeHidden){
            inputs.addAll(this.getEnvsItems());
            inputs.addAll(this.getSamplers());
        }
        inputs.addAll(this.getSampledSamplers());
        if(!CagedMobs.SERVER_CONFIG.areSpawnEggsDisabled()){
            SpawnEggItem spawnEgg = ForgeSpawnEggItem.fromEntityType(entityData.getEntityType());
            if(spawnEgg != null){
                inputs.addAll(new ArrayList<>(Collections.singleton(spawnEgg.getDefaultInstance())));
            }
        }
        return inputs;
    }

    @Override
    public void setIngredients(IIngredients iIngredients) {
        // Add input items
        iIngredients.setInputs(VanillaTypes.ITEM, this.getAllInputItemStacks(true));
        // Add outputs
        final List<ItemStack> outputs = new ArrayList<>();
        List<Item> blacklistedItems = RecipesHelper.getItemsFromConfigList();
        for(LootData loot : this.drops){
            // Add the item to the outputs list if it's not disabled in the config
            if(!CagedMobs.SERVER_CONFIG.isItemsListInWhitelistMode()){
                if(!blacklistedItems.contains(loot.getItem().getItem())){
                    outputs.add(loot.getItem());
                    if(!loot.getCookedItem().isEmpty() && loot.isCooking()) {
                        outputs.add(loot.getCookedItem());
                    }
                }
            }else{
                if(blacklistedItems.contains(loot.getItem().getItem())){
                    outputs.add(loot.getItem());
                    if(!loot.getCookedItem().isEmpty() && loot.isCooking()) {
                        outputs.add(loot.getCookedItem());
                    }
                }
            }

        }
        iIngredients.setOutputs(VanillaTypes.ITEM, outputs);
    }

    public void setRecipe(IRecipeLayout builder, EntityDataWrapper entityWrapper, IIngredients ingredients) {
        final IGuiItemStackGroup stacks = builder.getItemStacks();

        // Input items
        stacks.init(0, true, 14, 62+19);
        stacks.set(0, this.getAllInputItemStacks(false));

        // Soil Inputs
        stacks.init(1, true, 14 + 20, 62 + 19);
        stacks.set(1, entityWrapper.getEnvsItems());

        int nextSlotId = 2;
        List<Item> blacklistedItems = RecipesHelper.getItemsFromConfigList();
        for (final LootData entry : entityWrapper.getDrops()) {
            // If items not blacklisted draw them
            if(!CagedMobs.SERVER_CONFIG.isItemsListInWhitelistMode()){
                if(!blacklistedItems.contains(entry.getItem().getItem())){
                    int relativeSlotId = nextSlotId - 2;
                    stacks.init(nextSlotId, false, 100 + 19 * (relativeSlotId % 4), 5 + 19 * (relativeSlotId / 4));
                    if(entry.isCooking() && entityWrapper.getCookedIDs().contains(relativeSlotId)){
                        stacks.set(nextSlotId, entry.getCookedItem());
                    }else{
                        stacks.set(nextSlotId, entry.getItem());
                    }
                    nextSlotId++;
                }
            }else{
                if(blacklistedItems.contains(entry.getItem().getItem())){
                    int relativeSlotId = nextSlotId - 2;
                    stacks.init(nextSlotId, false, 100 + 19 * (relativeSlotId % 4), 5 + 19 * (relativeSlotId / 4));
                    if(entry.isCooking() && entityWrapper.getCookedIDs().contains(relativeSlotId)){
                        stacks.set(nextSlotId, entry.getCookedItem());
                    }else{
                        stacks.set(nextSlotId, entry.getItem());
                    }
                    nextSlotId++;
                }
            }
        }
        stacks.addTooltipCallback(entityWrapper::getTooltip);
    }

    public void draw(MatrixStack pGuiGraphics, IGuiHelper guiHelper) {
        // Draw Seed & Soil
        guiHelper.getSlotDrawable().draw(pGuiGraphics, 14, 62+19);
        guiHelper.getSlotDrawable().draw(pGuiGraphics, 14+20, 62 + 19);
        // Draw Drops
        for (int nextSlotId = 2; nextSlotId < 22; nextSlotId++) {
            final int relativeSlotId = nextSlotId - 2;
            guiHelper.getSlotDrawable().draw(pGuiGraphics, 100 + 19 * (relativeSlotId % 4), 5 + 19 * (relativeSlotId / 4));
        }
        // Create the entity object to render
        World level = Minecraft.getInstance().level;
        Optional<Entity> entity = EntityRendererHelper.createEntity(level, this.getEntityData().getEntityType(), null);
        // Render the entity if created correctly
        if(entity.isPresent()){
            rotation = (rotation+ 0.5f)% 360;
            EntityRendererHelper.renderEntity(pGuiGraphics, 33, 120, 38 - yaw, 70, rotation, entity.get() );
            // Update yaw
            yaw = (yaw + 1.5) % 720.0F;
        }
        // Draw entity name
        if(this.getEntityData() != null && this.getEntityData().getEntityType() != null) {
            Minecraft.getInstance().font.draw(pGuiGraphics, this.getEntityData().getEntityType().getDescription(), 5, 2, 8);
        }
        // Draw required ticks
        Minecraft.getInstance().font.draw(pGuiGraphics, new TranslationTextComponent("jei.tooltip.cagedmobs.entity.ticks", this.getSeconds()), 10, 102, 8);
        // Draw waterlogged info if it requires water
        if(this.ifRequiresWater()){
            Minecraft.getInstance().font.draw(pGuiGraphics, new TranslationTextComponent("jei.tooltip.cagedmobs.entity.requiresWater").withStyle(TextFormatting.BLUE), 5, 112, 8);
        }
    }

    public List<ItemStack> getSampledSamplers() {
        List<ItemStack> ret = NonNullList.create();
        for (ItemStack stack : samplers) {
            stack = stack.copy();
            EntityType<?> type = entityData.getEntityType();
            CompoundNBT nbt = new CompoundNBT();
            SerializationHelper.serializeEntityTypeNBT(nbt, type);
            stack.setTag(nbt);

            ret.add(stack);
        }
        return ret;
    }

    public void getTooltip (int slotIndex, boolean input, ItemStack ingredient, List<ITextComponent> tooltip) {
        if(!ingredient.isEmpty()){
            if(slotIndex == 1){
                EnvironmentData env = MobCageBlockEntity.getEnvironmentDataFromItemStack(ingredient);
                if(env != null){
                    tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.growModifier",  DECIMAL_FORMAT.format(env.getGrowModifier() * 100 - 100)));
                }
            }else if(slotIndex != 0){
                LootData loot = this.drops.get(slotIndex-2);
                tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.chance",  DECIMAL_FORMAT.format(loot.getChance() * 100)));
                if(loot.getMinAmount() == loot.getMaxAmount()){
                    tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.amountEqual",loot.getMinAmount()));
                }else{
                    tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.amount",loot.getMinAmount(), loot.getMaxAmount()));
                }
                if(loot.isLighting()){
                    tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.lightning_upgrade").withStyle(TextFormatting.YELLOW));
                }
                if(loot.isCooking() && ingredient.getItem().equals(loot.getCookedItem().getItem())){
                    tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.cooking_upgrade").withStyle(TextFormatting.YELLOW));
                }
                if(loot.isArrow()){
                    tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.arrow_upgrade").withStyle(TextFormatting.YELLOW));
                }
                if(loot.hasColor()){
                    tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.colorItem").withStyle(TextFormatting.YELLOW));
                }
            }
        }
    }
}

