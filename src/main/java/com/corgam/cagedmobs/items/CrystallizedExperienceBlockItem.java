package com.corgam.cagedmobs.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class CrystallizedExperienceBlockItem extends BlockItem {

    public CrystallizedExperienceBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.cagedmobs.crystallized_experience.info2").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.cagedmobs.crystallized_experience.info3").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(itemStack.getItem() instanceof CrystallizedExperienceBlockItem){
            // If on client side, just play the sound
            if(level.isClientSide()){
                level.playSound(player, player.getX(), player.getY()+0.5,player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (level.random.nextFloat() - level.random.nextFloat()) * 0.35F + 0.9F);
            // Do the logic on server side
            }else{
                // Consume the whole stack
                if (player.isCrouching()) {
                    for(int i = 0; i < itemStack.getCount(); i++){
                        player.giveExperiencePoints(9 * (level.random.nextInt(2) + 1));
                    }
                    if(!player.isCreative()) {
                        itemStack.setCount(0);
                    }
                    // Consume single item
                } else {
                    if(!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                    player.giveExperiencePoints(9 * (level.random.nextInt(2) + 1));
                }
            }
            InteractionResultHolder.success(itemStack);
        }
        return InteractionResultHolder.fail(itemStack);
    }

}
