package com.corgam.cagedmobs.items;

import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.mob.MobData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class DnaSamplerItem extends Item {
    public DnaSamplerItem(Properties properties) {
        super(properties);
    }

    // Called on left-click on an entity to get it's sample
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(target.level.isClientSide() || !(attacker instanceof Player)) return false;
        Player player = (Player) attacker;
        InteractionHand hand = player.getUsedItemHand();
        if (target.isAlive() && canBeCached(target)) {
            if(samplerTierSufficient(stack, target)) {
                CompoundTag nbt = new CompoundTag();
                SerializationHelper.serializeEntityTypeNBT(nbt, target.getType());
                // If sheep add it's color to nbt
                if(target instanceof Sheep){
                    Sheep sheep = (Sheep) target;
                    DyeColor color = sheep.getColor();
                    nbt.putInt("Color",color.getId());
                }
                stack.setTag(nbt);
                player.setItemInHand(hand, stack);
                return true;
            }else{
                player.displayClientMessage(new TranslatableComponent("item.cagedmobs.dnasampler.not_sufficient"), true);
            }
        }else{
            player.displayClientMessage(new TranslatableComponent("item.cagedmobs.dnasampler.not_cachable"), true);
        }
        return false;
    }

    // Checks if a sampler's tier is sufficient to sample given entity
    private static boolean samplerTierSufficient(ItemStack stack, Entity target) {
        EntityType<?> type = target.getType();
        boolean sufficient = false;
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                // Check for null exception
                if(mobData.getEntityType() == null){continue;}
                if(mobData.getEntityType().equals(type) && mobData.getSamplerTier() <= getSamplerTierInt(stack.getItem())) {
                    sufficient = true;
                    break;
                }
            }
        }
        return sufficient;
    }

    // Returns a tier number in a form of int from Item
    private static int getSamplerTierInt(Item item) {
        if(item instanceof DnaSamplerNetheriteItem){
            return 3;
        }else if(item instanceof DnaSamplerDiamondItem){
            return 2;
        }else{
            return 1;
        }
    }

    // Check if entity can be cached based on the list of cachable entities
    private static boolean canBeCached(Entity clickedEntity) {
        boolean contains = false;
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                // Check for null exception
                if(mobData.getEntityType() == null){continue;}
                if(mobData.getEntityType().equals(clickedEntity.getType())) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if(playerIn.isCrouching() && itemstack.hasTag()) {
            itemstack.removeTagKey("entity");
            playerIn.swing(handIn);
            InteractionResultHolder.success(itemstack);
        }
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(getTooltip(stack));
        tooltip.add(getInformationForTier().withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.cagedmobs.dnasampler.makeEmpty").withStyle(ChatFormatting.GRAY));
    }

    private TranslatableComponent getInformationForTier(){
        if(this instanceof DnaSamplerNetheriteItem){
            return new TranslatableComponent("item.cagedmobs.dnasampler.tier3Info");
        }else if(this instanceof DnaSamplerDiamondItem){
            return new TranslatableComponent("item.cagedmobs.dnasampler.tier2Info");
        }else{
            return new TranslatableComponent("item.cagedmobs.dnasampler.tier1Info");
        }
    }

    private Component getTooltip(ItemStack stack) {
        if(!DnaSamplerItem.containsEntityType(stack)) {
            return new TranslatableComponent("item.cagedmobs.dnasampler.empty").withStyle(ChatFormatting.YELLOW);
        }else {
            EntityType<?> type = SerializationHelper.deserializeEntityTypeNBT(stack.getTag());
            // Add the text component
            if(type != null){
                return new TranslatableComponent(type.getDescriptionId()).withStyle(ChatFormatting.YELLOW);
            }else{
                // If not found say Unknown entity for crash prevention
                return new TranslatableComponent("item.cagedmobs.dnasampler.unknown_entity").withStyle(ChatFormatting.YELLOW);
            }
        }
    }

    public static boolean containsEntityType(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag() != null && stack.getTag().contains("entity");
    }

    public void removeEntityType(ItemStack stack) {
        stack.removeTagKey("entity");
    }

    public EntityType<?> getEntityType(ItemStack stack) {
        if(stack.hasTag() && stack.getTag() != null) {
            return SerializationHelper.deserializeEntityTypeNBT(stack.getTag());
        }else {
            return null;
        }
    }
}
