package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.tileEntities.MobCageTE;
import net.minecraft.ChatFormatting;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Properties;

public class HoppingMobCageBlock extends MobCageBlock{
    public HoppingMobCageBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MobCageTE(true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.mainInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.hoppingInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.envInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.upgrading").withStyle(ChatFormatting.GRAY));
    }
}
