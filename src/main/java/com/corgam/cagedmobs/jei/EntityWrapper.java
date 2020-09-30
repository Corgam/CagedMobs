package com.corgam.cagedmobs.jei;

import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SquidEntity;
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

    private static double yaw = 0;

    public EntityWrapper(MobData entity){
        this.entity = entity;
        // Read valid envs based on entity
        for(EnvironmentData env : RecipesHelper.getEnvsRecipesList(RecipesHelper.getRecipeManager())){
            if(RecipesHelper.isEnvValidForEntity(entity,env)){
                this.envs.addAll(Arrays.asList(env.getInputItem().getMatchingStacks()));
            }
        }
        this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER.get()));
        this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER_DIAMOND.get()));
        this.samplers.add(new ItemStack(CagedItems.DNA_SAMPLER_NETHERITE.get()));
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
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack matrixStack, double mouseX, double mouseY) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("id", Registry.ENTITY_TYPE.getKey(this.getEntity().getEntityType()).toString());
        WeightedSpawnerEntity renderedEntity = new WeightedSpawnerEntity(1, nbt);
        LivingEntity entity = (LivingEntity) EntityType.func_220335_a(renderedEntity.getNbt(), Minecraft.getInstance().getIntegratedServer().getWorlds().iterator().next(), Function.identity());

        float scale = 20.0F;
        int offsetY = getOffsetForEntityType(entity);
        renderEntity(
                matrixStack,
                40, 105 - offsetY, scale,
                38 - yaw,
                70 - offsetY,
                entity
        );
        // Update yaw
        yaw = (yaw + 1.5) % 720.0F;
    }

    public static void renderEntity(MatrixStack matrixStack, int x, int y, double scale, double yaw, double pitch, LivingEntity livingEntity) {
        if (livingEntity.world == null) livingEntity.world = Minecraft.getInstance().world;
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
        RenderSystem.translatef(x, y, 50.0F);
        RenderSystem.scalef((float) -scale, (float) scale, (float) scale);
        MatrixStack mobMatrix = new MatrixStack();
        // Flip entity
        mobMatrix.rotate(Vector3f.ZP.rotationDegrees(180.0F));
        // Rotate entity
        RenderSystem.rotatef(((float) Math.atan((-40 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        livingEntity.renderYawOffset = (float) (yaw/40.F) * 20.0F;
        livingEntity.rotationYaw = (float) (yaw/40.F) * 20.0F;
        livingEntity.rotationYawHead = livingEntity.rotationYaw;
        livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
        mobMatrix.translate(0.0F, livingEntity.getYOffset(), 0.0F);
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        RenderSystem.runAsFancy(() -> {
            entityrenderermanager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, mobMatrix, renderTypeBuffer, 15728880);
        });
        renderTypeBuffer.finish();
        entityrenderermanager.setRenderShadow(true);
        RenderSystem.popMatrix();
    }

    private int getOffsetForEntityType(LivingEntity entity){
        if(entity instanceof SquidEntity){
            return 70;
        }else if(entity instanceof AbstractGroupFishEntity){
            return 60;
        }
        else{
            return 50;
        }
    }


    @Override
    public void setIngredients(IIngredients iIngredients) {

        // Inputs
        final List<ItemStack> inputs = new ArrayList<>();

        inputs.addAll(this.envs);

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

    public void getTooltip (int slotIndex, boolean input, ItemStack ingredient, List<ITextComponent> tooltip) {
        if(!ingredient.isEmpty()){
            if(slotIndex != 0 && slotIndex != 1){
                 LootData loot = this.drops.get(slotIndex-2);
                 tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.chance",loot.getChance()*100f));
                 if(loot.getMinAmount() == loot.getMaxAmount()){
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.amountEqual",loot.getMinAmount()));
                 }else{
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.amount",loot.getMinAmount(), loot.getMaxAmount()));
                 }
                 if(loot.isLighting()){
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.lightning_upgrade").func_240699_a_(TextFormatting.YELLOW));
                 }
                 if(loot.isCooking() && ingredient.getItem().equals(loot.getCookedItem().getItem())){
                     tooltip.add(new TranslationTextComponent("jei.tooltip.cagedmobs.entity.cooking_upgrade").func_240699_a_(TextFormatting.YELLOW));
                 }
            }
        }
    }

}
