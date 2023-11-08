package com.corgam.cagedmobs.items.upgrades;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class UpgradeItem extends Item {
    public UpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.cagedmobs.upgrades.attach").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.cagedmobs.upgrades.stack").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
