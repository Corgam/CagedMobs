package com.corgam.cagedmobs.blocks;

import net.minecraft.block.SlimeBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class CrystallizedExperienceBlock extends SlimeBlock {
    public CrystallizedExperienceBlock(Properties props) {
        super(props);
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable IBlockReader pLevel, List<ITextComponent> tooltip, ITooltipFlag pFlag) {
        tooltip.add(new TranslationTextComponent("item.cagedmobs.crystallized_experience.info2").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.cagedmobs.crystallized_experience.info3").withStyle(TextFormatting.GRAY));
    }
}
