package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blockEntities.MobCageBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class HoppingMobCageBlock extends MobCageBlock{
    public HoppingMobCageBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MobCageBlockEntity(pos, state, true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack item, @Nullable BlockGetter getter, List<Component> tooltip, TooltipFlag flag) {
        if(CagedMobs.SERVER_CONFIG.ifHoppingCagesDisabled()){
            tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.hoppingCagesDisabled1").withStyle(ChatFormatting.RED));
            tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.hoppingCagesDisabled2").withStyle(ChatFormatting.RED));
        }
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.mainInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.hoppingInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.envInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.upgrading").withStyle(ChatFormatting.GRAY));
    }
}
