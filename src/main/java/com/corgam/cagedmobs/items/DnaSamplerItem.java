package com.corgam.cagedmobs.items;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.mob.MobData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class DnaSamplerItem extends Item {
    public DnaSamplerItem(Properties properties) {
        super(properties);
    }

    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target,
                                                     Hand hand) {
        if(target.getEntityWorld().isRemote) return ActionResultType.FAIL;
        if (target.isAlive() && canBeCached(target)) {
            if(samplerTierSufficient(target)) {
                CompoundNBT nbt = new CompoundNBT();
                SerializationHelper.serializeEntityTypeNBT(nbt, target.getType());
                stack.setTag(nbt);
                playerIn.swingArm(hand);
                playerIn.setHeldItem(hand, stack);
                return ActionResultType.func_233537_a_(playerIn.world.isRemote);
            }else{
                playerIn.sendStatusMessage(new TranslationTextComponent("item.cagedmobs.dnasampler.not_sufficient"), true);
                return ActionResultType.PASS;
            }
        }else{
            playerIn.sendStatusMessage(new TranslationTextComponent("item.cagedmobs.dnasampler.not_cachable"), true);
            return ActionResultType.PASS;
        }
    }

    private boolean samplerTierSufficient(LivingEntity target) {
        EntityType<?> type = target.getType();
        boolean sufficient = false;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                if(mobData.getEntityType().equals(type) && mobData.getSamplerTier() <= getSamplerTierInt()) {
                    sufficient = true;
                    break;
                }
            }
        }
        return sufficient;
    }

    private int getSamplerTierInt() {
        if(this instanceof DnaSamplerNetheriteItem){
            return 3;
        }else if(this instanceof DnaSamplerDiamondItem){
            return 2;
        }else{
            return 1;
        }
    }

    // Check if entity can be cached based on the list of cachable entities
    private boolean canBeCached(Entity clickedEntity) {
        boolean contains = false;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                if(mobData.getEntityType().equals(clickedEntity.getType())) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if(playerIn.isSneaking() && itemstack.hasTag()) {
            itemstack.removeChildTag("entity");
            playerIn.swingArm(handIn);
            ActionResult.resultSuccess(itemstack);
        }
        return ActionResult.resultFail(itemstack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(getTooltip(stack));
        tooltip.add(getInformationForTier().func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.cagedmobs.dnasampler.makeEmpty").func_240699_a_(TextFormatting.GRAY));
    }

    private TranslationTextComponent getInformationForTier(){
        if(this instanceof DnaSamplerNetheriteItem){
            return new TranslationTextComponent("item.cagedmobs.dnasampler.tier3Info");
        }else if(this instanceof DnaSamplerDiamondItem){
            return new TranslationTextComponent("item.cagedmobs.dnasampler.tier2Info");
        }else{
            return new TranslationTextComponent("item.cagedmobs.dnasampler.tier1Info");
        }
    }

    private ITextComponent getTooltip(ItemStack stack) {
        if(!DnaSamplerItem.containsEntityType(stack)) {
            return new TranslationTextComponent("item.cagedmobs.dnasampler.empty").func_240699_a_(TextFormatting.YELLOW);
        }else {
            // Debug only
            // return new StringTextComponent(stack.getTag().getString("entity")).func_240699_a_(TextFormatting.YELLOW);
            ResourceLocation res = new ResourceLocation(stack.getTag().getString("entity"));
            EntityType<?> type = EntityType.byKey(res.getPath()).get();
            return new TranslationTextComponent(type.getTranslationKey()).func_240699_a_(TextFormatting.YELLOW);
        }
    }

    public static boolean containsEntityType(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().contains("entity");
    }

    public void removeEntityType(ItemStack stack) {
        stack.removeChildTag("entity");
    }

    public EntityType<?> getEntityType(ItemStack stack) {
        if(stack.hasTag() && stack.getTag() != null) {
            return SerializationHelper.deserializeEntityTypeNBT(stack.getTag());
        }else {
            return null;
        }
    }
}
