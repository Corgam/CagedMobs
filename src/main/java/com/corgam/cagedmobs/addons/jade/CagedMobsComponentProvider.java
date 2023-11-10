package com.corgam.cagedmobs.addons.jade;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import mcjty.theoneprobe.api.IElement;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.List;

public class CagedMobsComponentProvider implements IComponentProvider {

    public ResourceLocation getUid() {
        return new ResourceLocation(CagedMobs.MOD_ID, "cagedmobs_jade");
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor blockAccessor, IPluginConfig config) {
        if(!(blockAccessor.getTileEntity() instanceof MobCageBlockEntity)){
            return;
        }
        MobCageBlockEntity tile = (MobCageBlockEntity) blockAccessor.getTileEntity();
        // Add growth progress
        if(tile.hasEntity() && tile.hasEnvironment()){
//            tooltip.add(helper.progress(tile.getGrowthPercentage(),
//                    new TextComponent(String.format("%3.0f%%", tile.getGrowthPercentage() * 100)),
//                    helper.progressStyle().color(0xff44AA44, 0xff44AA44),
//                    helper.borderStyle()
//            ));
            tooltip.add(new TranslationTextComponent(String.format("%3.0f%%", tile.getGrowthPercentage() * 100)));
        }
        // Add Environment
        if(tile.hasEnvironment()){
            ItemStack representation = tile.getEnvironmentItemStack();
            if(representation != null){
                tooltip.add(new TranslationTextComponent("JADE.tooltip.cagedmobs.cage.environment"));
                tooltip.add(representation.getHoverName());
//                tooltip.add(List.of(
//                        helper.item(representation, 1.0F),
//                        helper.text(representation.getHoverName())));
            }
        }
        // Add Entity
        if(tile.hasEntity()){
            EntityType<?> representation = tile.getEntityType();
            if(representation != null){
                tooltip.add(new StringTextComponent(
                        new TranslationTextComponent("JADE.tooltip.cagedmobs.cage.entity").withStyle(TextFormatting.GRAY).getString() +
                                new TranslationTextComponent(representation.getDescriptionId()).withStyle(TextFormatting.GRAY).getString()));
            }
        }
        // Add Upgrades
        if(tile.hasAnyUpgrades()){
            // Add Upgrade text
            tooltip.add(new TranslationTextComponent("TOP.tooltip.cagedmobs.cage.upgrades"));
            // Iterate through upgrades
            List<IElement> upgrades = new ArrayList<>();
            for(ItemStack upgrade : tile.getUpgradesAsItemStacks()){
                if(!upgrade.isEmpty()){
//                    upgrades.add(helper.item(upgrade));
                }
            }
            // Render a list of upgrades
//            tooltip.add(upgrades);
        }
    }
}
