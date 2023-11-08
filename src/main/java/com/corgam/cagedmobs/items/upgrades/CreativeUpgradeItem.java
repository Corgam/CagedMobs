package com.corgam.cagedmobs.items.upgrades;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CreativeUpgradeItem extends UpgradeItem{
    public CreativeUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.cagedmobs.creative_upgrade.info").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.cagedmobs.upgrades.attach").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("item.cagedmobs.creative_upgrade.info2").withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean isFoil(@NotNull ItemStack itemStack) {
        return true;
    }
}
