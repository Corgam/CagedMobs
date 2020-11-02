package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.tileEntities.MobCageTE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class HoppingMobCageBlock extends MobCageBlock{
    public HoppingMobCageBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new MobCageTE(true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.mainInfo").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.hoppingInfo").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.envInfo").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.upgrading").mergeStyle(TextFormatting.GRAY));
    }
}
