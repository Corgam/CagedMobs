package com.corgam.cagedmobs.items;

import com.corgam.cagedmobs.CagedMobs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EmptySpawnEggItem extends Item {
    public EmptySpawnEggItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.cagedmobs.dna_sampler.getBackEntity").withStyle(ChatFormatting.GRAY));
        if(CagedMobs.SERVER_CONFIG.areSpawnEggsDisabled()){
            tooltip.add(Component.translatable("item.cagedmobs.dna_sampler.disabled").withStyle(ChatFormatting.RED));
        }
    }
}
