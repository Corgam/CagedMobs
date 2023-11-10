package com.corgam.cagedmobs.items;

import com.corgam.cagedmobs.CagedMobs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EmptySpawnEggItem extends Item {
    public EmptySpawnEggItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.cagedmobs.dna_sampler.getBackEntity").withStyle(TextFormatting.GRAY));
        if(CagedMobs.SERVER_CONFIG.areSpawnEggsDisabled()){
            tooltip.add(new TranslationTextComponent("item.cagedmobs.dna_sampler.disabled").withStyle(TextFormatting.RED));
        }
    }
}
