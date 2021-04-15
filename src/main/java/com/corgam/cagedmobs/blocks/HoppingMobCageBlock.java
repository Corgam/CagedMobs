package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.CagedMobs;
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
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new MobCageTE(true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(CagedMobs.SERVER_CONFIG.ifHoppingCagesDisabled()){
            tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.hoppingCagesDisabled1").withStyle(TextFormatting.RED));
            tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.hoppingCagesDisabled2").withStyle(TextFormatting.RED));
        }
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.mainInfo").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.hoppingInfo").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.envInfo").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.upgrading").withStyle(TextFormatting.GRAY));
    }
}
