package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blockEntities.MobCageBlockEntity;
import com.corgam.cagedmobs.registers.CagedItems;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EntityWrapper implements IRecipeCategoryExtension {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private final MobData entity;
    private final List<ItemStack> envs = NonNullList.create();
    private final List<LootData> drops = NonNullList.create();
    private final List<ItemStack> samplers = NonNullList.create();
    private final List<Integer> cookedIDs = new ArrayList<Integer>();
    private final boolean requiresWater;
    private final int ticks;
    public static float rotation = 0.0f;

    private static double yaw = 0;

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
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE.get(), RecipesHelper.getRecipeManager()).values()) {
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

    public MobData getEntity() {
        return entity;
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

    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
        // DNA Samplers
        final IRecipeSlotBuilder samplersSlot = builder.addSlot(RecipeIngredientRole.INPUT, 15, 62+20);
        samplersSlot.addItemStacks(this.getSamplers());

        // Soil Inputs
        final IRecipeSlotBuilder environmentsSlot = builder.addSlot(RecipeIngredientRole.INPUT, 15 + 20, 62+20);
        environmentsSlot.addItemStacks(this.getEnvsItems());
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

    public void draw(IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY, IGuiHelper guiHelper, IDrawable background, int width, int height) {
        // Draw Seed & Soil
        guiHelper.getSlotDrawable().draw(graphics, 14, 62+19);
        guiHelper.getSlotDrawable().draw(graphics, 14+20, 62 + 19);
        // Draw Drops
        for (int nextSlotId = 2; nextSlotId < 22; nextSlotId++) {
            final int relativeSlotId = nextSlotId - 2;
            guiHelper.getSlotDrawable().draw(graphics, 100 + 19 * (relativeSlotId % 4), 5 + 19 * (relativeSlotId / 4));
        }
        // Draw entity
        this.drawEntity(graphics, background.getWidth(), background.getHeight(), mouseX, mouseY, width, height);
        // Draw entity name
        if(this.getEntity() != null && this.getEntity().getEntityType() != null) {
            graphics.drawString(Minecraft.getInstance().font, this.getEntity().getEntityType().getDescription(), 5, 2, 8, false);
        }
        // Draw required ticks
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.tooltip.cagedmobs.entity.ticks", this.getSeconds()), 10, 102, 8, false);
        // Draw waterlogged info if it requires water
        if(this.ifRequiresWater()){
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.tooltip.cagedmobs.entity.requiresWater", this.getSeconds()).withStyle(ChatFormatting.BLUE), 5, 112, 8, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void drawEntity(GuiGraphics graphics, int recipeWidth, int recipeHeight, double mouseX, double mouseY, int width, int height) {
        // Proper entity
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", EntityType.getKey(this.getEntity().getEntityType()).toString());
        SpawnData renderedEntity = new SpawnData(nbt, Optional.empty());
        if(Minecraft.getInstance().getSingleplayerServer() != null){ // When at single player or single player server
            LivingEntity livingEntity = (LivingEntity) EntityType.loadEntityRecursive(renderedEntity.getEntityToSpawn(), Minecraft.getInstance().getSingleplayerServer().overworld(), Function.identity());
            if (livingEntity != null) {
                float scale = getScaleForEntityType(livingEntity);
                int offsetY = getOffsetForEntityType(livingEntity);
                // Add rotation
                rotation = (rotation+ 0.5f)% 360;
                // Render the entity
                renderEntity(
                        graphics,
                        33, 120 - offsetY, scale,
                        38 - yaw,
                        70 - offsetY,
                        livingEntity
                );
                // Update yaw
                yaw = (yaw + 1.5) % 720.0F;
            }
        }else if(Minecraft.getInstance().getCurrentServer() != null){ // When at dedicated server
            Level level = Minecraft.getInstance().level;
            if(level != null){
                LivingEntity livingEntity = (LivingEntity) EntityType.loadEntityRecursive(renderedEntity.getEntityToSpawn(), level, Function.identity());
                if (livingEntity != null) {
                    float scale = getScaleForEntityType(livingEntity);
                    int offsetY = getOffsetForEntityType(livingEntity);
                    // Add rotation
                    rotation = (rotation+ 0.5f)% 360;
                    // Render the entity
                    renderEntity(
                            graphics,
                            33, 120 - offsetY, scale,
                            38 - yaw,
                            70 - offsetY,
                            livingEntity
                    );
                    // Update yaw
                    yaw = (yaw + 1.5) % 720.0F;
                }
            }
        }
    }

    public static void renderEntity(GuiGraphics graphics, int x, int y, float scale, double yaw, double pitch, LivingEntity entity) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        // Translate the entity to right position
        matrixStack.translate(x, y, 50F);
        // Scale the entity
        matrixStack.scale(-scale, scale, scale);
        // Rotate the entity so it's not upside down
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
        // Rotate the entity around
        matrixStack.mulPose(Axis.YP.rotationDegrees(rotation));
        // Render the entity
        MultiBufferSource.BufferSource buff = Minecraft.getInstance().renderBuffers().bufferSource();
        try{
            Minecraft.getInstance().getEntityRenderDispatcher().render(entity,0.0D,0.0D,0.0D,0.0F,0.0F,matrixStack,buff,15728880);
        }catch (Exception e){
            matrixStack.translate(x, y, -50F);
            matrixStack.scale(scale, -scale, -scale);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
            CagedMobs.LOGGER.error("[CagedMobs] Error with rendering entity in JEI!", e);
        }
        buff.endBatch();
        matrixStack.popPose();
    }

    private IRecipeSlotTooltipCallback getEnvTooltip() {
        return (view, tooltip) -> {
            if(view.getDisplayedItemStack().isPresent()){
                ItemStack displayedItem = view.getDisplayedItemStack().get();
                EnvironmentData env = MobCageBlockEntity.getEnvironmentFromItemStack(displayedItem);
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

    private float getScaleForEntityType(LivingEntity entity){
        float width = entity.getBbWidth();
        float height = entity.getBbHeight();
        // Some hardcoded values
        if(entity.getType().toString().contains("twilightforest.ur_ghast")){return 2.0F;}
        if(entity.getType().toString().contains("greekfantasy.charybdis")){return 3.0F;}
        if(entity.getType().toString().contains("twilightforest.hydra")){return 3.2F;}
        if(entity.getType().toString().contains("twilightforest.yeti_alpha")){return 6.0F;}
        if(entity.getType().toString().contains("minecraft.ender_dragon")) {return 5.0F;}
        if(entity.getType().toString().contains("twilightforest.armored_giant")) {return 6.0F;}
        if(entity.getType().toString().contains("twilightforest.giant_miner")) {return 6.0F;}
        if(entity.getType().toString().contains("twilightforest.mini_ghast")) {return 10.0F;}
        if(entity.getType().toString().contains("outvoted:kraken")) {return 2.0F;}
        if(entity.getType().toString().contains("mowziesmobs:frostmaw")) {return 10.0F;}
        if(entity.getType().toString().contains("alexsmobs:cachalot_whale")) {return 0.2F;}
        if(entity.getType().toString().contains("greekfantasy:giant_boar")) {return 10.0F;}
        if(entity.getType().toString().contains("alexsmobs:crocodile")) {return 6.0F;}
        if(entity.getType().toString().contains("alexsmobs:hammerhead_shark")) {return 6.0F;}
        if(entity.getType().toString().contains("upgrade_aquatic:great_thrasher")) {return 2.0F;}
        if(entity.getType().toString().contains("fireandice:cyclops")) {return 2.0F;}
        if(entity.getType().toString().contains("alexsmobs:cachalot_whale")) {return 0.5F;}
        if(entity.getType().toString().contains("alexsmobs:laviathan")) {return 0.5F;}
        if(entity.getType().toString().contains("alexsmobs:void_worm")) {return 1.0F;}
        if(entity instanceof ElderGuardian) {return 10.0F;}
        if(entity instanceof  AbstractFish) {return 25.0F;}
        if(entity instanceof Ghast) {return 5.2F;}

        // Handling some of the other cases
        if(width <= height){
            if(height >= 3){
                return 10.0f;
            }else if(height >= 2.5){
                return 15.0f;
            }
            else if(height >= 1.9){
                return 18.0F;
            }
        }
        return 20.0F;
    }

    private int getOffsetForEntityType(LivingEntity entity){
        if(entity instanceof Phantom ||
                entity instanceof AbstractFish ||
                entity instanceof EnderDragon ||
                entity instanceof Dolphin ||
                entity instanceof Guardian ||
                entity instanceof Turtle
        ){
            return 60;
        }else if(entity instanceof Ghast || entity instanceof Squid){
            return 65;
        }
        return 50;
    }
}

