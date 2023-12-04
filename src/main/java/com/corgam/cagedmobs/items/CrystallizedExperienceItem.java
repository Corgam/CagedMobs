package com.corgam.cagedmobs.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class CrystallizedExperienceItem extends Item {

    public CrystallizedExperienceItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.cagedmobs.crystallized_experience.info").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.cagedmobs.crystallized_experience.info2").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.cagedmobs.crystallized_experience.info3").withStyle(TextFormatting.GRAY));
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(itemStack.getItem() instanceof CrystallizedExperienceItem){
            // If on client side, just play the sound
            if(level.isClientSide()){
                level.playSound(player, player.getX(), player.getY()+0.5,player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (level.random.nextFloat() - level.random.nextFloat()) * 0.35F + 0.9F);
            // Do the logic on server side
            }else{
                // Consume the whole stack
                if (player.isCrouching()) {
                    for(int i = 0; i < itemStack.getCount(); i++){
                        player.giveExperiencePoints(level.random.nextInt(2) + 1);
                    }
                    if(!player.isCreative()) {
                        itemStack.setCount(0);
                    }
                    // Consume single item
                } else {
                    if(!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                    player.giveExperiencePoints(level.random.nextInt(2) + 1);
                }
            }
            ActionResult.success(itemStack);
        }
        return ActionResult.fail(itemStack);
    }

}
