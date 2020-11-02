package com.corgam.cagedmobs.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class CookingUpgradeItem extends UpgradeItem{
    public CookingUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("item.cagedmobs.cookingupgrade.info").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.cagedmobs.upgrades.attach").mergeStyle(TextFormatting.GRAY));
    }
}
