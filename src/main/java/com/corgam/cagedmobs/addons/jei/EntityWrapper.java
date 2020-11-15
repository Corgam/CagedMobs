package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.fish.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class EntityWrapper implements IRecipeCategoryExtension {

    private final MobData entity;
    private final List<ItemStack> envs = NonNullList.create();
    private final List<LootData> drops = NonNullList.create();
    private final List<ItemStack> samplers = NonNullList.create();
    private final List<Integer> cookedIDs = new ArrayList<Integer>();
    private final int ticks;
    public static float rotation = 0.0f;


    private static double yaw = 0;

    public EntityWrapper(MobData entity){
        this.entity = entity;
        // Read valid envs based on entity
        for(EnvironmentData env : RecipesHelper.getEnvsRecipesList(RecipesHelper.getRecipeManager())){
            if(RecipesHelper.isEnvValidForEntity(entity,env)){
                this.envs.addAll(Arrays.asList(env.getInputItem().getMatchingStacks()));
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
        for(LootData data : entity.getResults()){
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
        // Set up required ticks
        this.ticks = entity.getTotalGrowTicks();
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack matrixStack, double mouseX, double mouseY) {
        // Proper entity
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("id", Registry.ENTITY_TYPE.getKey(this.getEntity().getEntityType()).toString());
        WeightedSpawnerEntity renderedEntity = new WeightedSpawnerEntity(1, nbt);
        // Get the world
        if(Minecraft.getInstance().getIntegratedServer() != null){
            LivingEntity livingEntity = (LivingEntity) EntityType.loadEntityAndExecute(renderedEntity.getNbt(), Minecraft.getInstance().getIntegratedServer().getWorlds().iterator().next(), Function.identity());
            if (livingEntity != null) {
                float scale = getScaleForEntityType(livingEntity);
                int offsetY = getOffsetForEntityType(livingEntity);
                // Add rotation
                rotation = (rotation+ 0.5f)% 360;
                // Render the entity
                renderEntity(
                        matrixStack,
                        38, 120 - offsetY, scale,
                        38 - yaw,
                        70 - offsetY,
                        livingEntity
                );
                // Update yaw
                yaw = (yaw + 1.5) % 720.0F;
            }
        }
    }



    public static void renderEntity(MatrixStack matrixStack, int x, int y, float scale, double yaw, double pitch, LivingEntity entity) {
        matrixStack.push();
        // Translate the entity to right position
        matrixStack.translate(x, y, 50F);
        // Scale the entity
        matrixStack.scale(-scale, scale, scale);
        // Rotate the entity so it's not upside down
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));
        // Rotate the entity around
        matrixStack.rotate(Vector3f.YP.rotationDegrees(rotation));

        // Render the entity
        IRenderTypeBuffer.Impl buff = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        try{
            Minecraft.getInstance().getRenderManager().renderEntityStatic(entity,0.0D,0.0D,0.0D,0.0F,0.0F,matrixStack,buff,15728880);
        }catch (Exception e){
            matrixStack.translate(x, y, -50F);
            matrixStack.scale(scale, -scale, -scale);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));

            CagedMobs.LOGGER.error("Error with rendering entity in JEI!(CagedMobs)", e);
        }
        buff.finish();
        matrixStack.pop();
    }


    private float getScaleForEntityType(LivingEntity entity){
        float width = entity.getWidth();
        float height = entity.getHeight();
        // Some hardcoded values
        if(entity instanceof GhastEntity){
            return 6.8F;
        }else if(entity instanceof EnderDragonEntity){
            return 5.2F;
        }else if(entity instanceof  AbstractGroupFishEntity){
            return 25.0F;
        }else if(entity instanceof ElderGuardianEntity){
            return 10.0F;
        }

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
        if(entity instanceof PhantomEntity||
            entity instanceof AbstractGroupFishEntity ||
                entity instanceof EnderDragonEntity ||
                entity instanceof DolphinEntity ||
                entity instanceof GuardianEntity ||
                entity instanceof TurtleEntity
        ){
            return 60;
        }else if(entity instanceof GhastEntity || entity instanceof SquidEntity){
            return 65;
        }
        return 50;
    }


    @Override
    public void setIngredients(IIngredients iIngredients) {
        // Inputs
        final List<ItemStack> inputs = new ArrayList<>(this.envs);
        iIngredients.setInputs(VanillaTypes.ITEM, inputs);

        // Outputs
        final List<ItemStack> outputs = new ArrayList<>();
        for(LootData loot : this.drops){
            outputs.add(loot.getItem());
            if(!loot.getCookedItem().isEmpty() && loot.isCooking()) {
                outputs.add(loot.getCookedItem());
            }
        }
        iIngredients.setOutputs(VanillaTypes.ITEM, outputs);
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

    public void getTooltip (int slotIndex, boolean input, ItemStack ingredient, List<ITextComponent> tooltip) {
        if(!ingredient.isEmpty()){
            if(slotIndex != 0 && slotIndex != 1){
                 LootData loot = this.drops.get(slotIndex-2);
                 tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.chance",  loot.getChance() * 100));
                 if(loot.getMinAmount() == loot.getMaxAmount()){
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.amountEqual",loot.getMinAmount()));
                 }else{
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.amount",loot.getMinAmount(), loot.getMaxAmount()));
                 }
                 if(loot.isLighting()){
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.lightning_upgrade").mergeStyle(TextFormatting.YELLOW));
                 }
                 if(loot.isCooking() && ingredient.getItem().equals(loot.getCookedItem().getItem())){
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.cooking_upgrade").mergeStyle(TextFormatting.YELLOW));
                 }
                 if(loot.isArrow()){
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.arrow_upgrade").mergeStyle(TextFormatting.YELLOW));
                 }
                 if(loot.hasColor()){
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.colorItem").mergeStyle(TextFormatting.YELLOW));
                 }
            }
        }
    }

}
