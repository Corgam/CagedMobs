package com.corgam.cagedmobs.addons.hwyla;

import com.corgam.cagedmobs.tileEntities.MobCageTE;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class CagedMobsComponentProvider implements IComponentProvider {

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if(!(accessor.getTileEntity() instanceof MobCageTE)){
            return;
        }
        MobCageTE tile = (MobCageTE) accessor.getTileEntity();
        // Add growth progress
        if(tile.hasEntity() && tile.hasEnvironment()){
            tooltip.add(new StringTextComponent(
                    new TranslationTextComponent("HWYLA.tooltip.cagedmobs.cage.progress").withStyle(TextFormatting.GRAY).getString() +
                    String.format("%s%.1f%%", TextFormatting.YELLOW, tile.getGrowthPercentage()*100)));
        }
        // Add env
        if(tile.hasEnvironment()){
            ItemStack representation = tile.getEnvItem();
            if(representation != null){
                tooltip.add(new StringTextComponent(
                        new TranslationTextComponent("HWYLA.tooltip.cagedmobs.cage.environment").getString() +
                                representation.getDisplayName().getString()
                ));
            }
        }
        // Add entity
        if(tile.hasEntity()){
            EntityType<?> representation = tile.getEntityType();
            if(representation != null){
                tooltip.add(new StringTextComponent(
                        new TranslationTextComponent("HWYLA.tooltip.cagedmobs.cage.entity").withStyle(TextFormatting.GRAY).getString() +
                        new TranslationTextComponent(representation.getDescriptionId()).withStyle(TextFormatting.GRAY
                        ).getString()));
            }
        }
        // Add upgrades
        if(tile.isLightning()){
            tooltip.add(new TranslationTextComponent("item.cagedmobs.lightningupgrade").withStyle(TextFormatting.GRAY));
        }
        if(tile.isCooking()){
            tooltip.add(new TranslationTextComponent("item.cagedmobs.cookingupgrade").withStyle(TextFormatting.GRAY));
        }
        if (tile.isArrow()) {
            tooltip.add(new TranslationTextComponent("item.cagedmobs.arrowupgrade").withStyle(TextFormatting.GRAY));
        }
    }

}
