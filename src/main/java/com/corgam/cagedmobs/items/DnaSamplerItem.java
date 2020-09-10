package com.corgam.cagedmobs.items;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.mob.MobData;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class DnaSamplerItem extends Item {
    public DnaSamplerItem(Properties properties) {
        super(properties);
    }

    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target,
                                                     Hand hand) {
        if(target.getEntityWorld().isRemote) return ActionResultType.FAIL;
        if (target.isAlive() && canBeCached(target)) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("entity", EntityType.getKey(target.getType()).toString());
            stack.setTag(nbt);
            playerIn.swingArm(hand);
            playerIn.setHeldItem(hand, stack);
            return ActionResultType.func_233537_a_(playerIn.world.isRemote);
        }
        playerIn.sendStatusMessage(new TranslationTextComponent("item.cagedmobs.dnasampler.not_cachable"), true);
        return ActionResultType.PASS;
    }

    // Check if entity can be cached based on the list of cachable entities
    private boolean canBeCached(Entity clickedEntity) {
        boolean contains = false;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(CagedMobs.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
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
        tooltip.add(new TranslationTextComponent("item.cagedmobs.dnasampler.usedFor").func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.cagedmobs.dnasampler.makeEmpty").func_240699_a_(TextFormatting.GRAY));
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
        if(stack.hasTag()) {
            ResourceLocation res = new ResourceLocation(stack.getTag().getString("entity"));
            //String typeString = stack.getTag().getString("entity");
            //ResourceLocation resourceLocation = ResourceLocation.tryCreate(typeString);
            System.out.println(res.getPath());
            return EntityType.byKey(res.getPath()).get();
        }else {
            return null;
        }
    }
}
