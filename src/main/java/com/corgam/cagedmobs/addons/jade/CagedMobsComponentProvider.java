package com.corgam.cagedmobs.addons.jade;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.api.ui.IElementHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CagedMobsComponentProvider implements IComponentProvider {

    public ResourceLocation getUid() {
        return new ResourceLocation(CagedMobs.MOD_ID, "cagedmobs_jade");
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if(!(blockAccessor.getBlockEntity() instanceof MobCageBlockEntity tile)){
            return;
        }
        IElementHelper helper = tooltip.getElementHelper();
        // Add growth progress
        if(tile.hasEntity() && tile.hasEnvironment()){
            tooltip.add(helper.progress(tile.getGrowthPercentage(),
                    new TextComponent(String.format("%3.0f%%", tile.getGrowthPercentage() * 100)),
                    helper.progressStyle().color(0xff44AA44, 0xff44AA44),
                    helper.borderStyle()
            ));
        }
        // Add Environment
        if(tile.hasEnvironment()){
            ItemStack representation = tile.getEnvironmentItemStack();
            if(representation != null){
                tooltip.add(new TranslatableComponent("JADE.tooltip.cagedmobs.cage.environment"));
                tooltip.add(List.of(
                        helper.item(representation, 1.0F),
                        helper.text(representation.getHoverName())));
            }
        }
        // Add Entity
        if(tile.hasEntity()){
            EntityType<?> representation = tile.getEntityType();
            if(representation != null){
                tooltip.add(new TextComponent(
                        new TranslatableComponent("JADE.tooltip.cagedmobs.cage.entity").withStyle(ChatFormatting.GRAY).getString() +
                                new TranslatableComponent(representation.getDescriptionId()).withStyle(ChatFormatting.GRAY).getString()));
            }
        }
        // Add Upgrades
        if(tile.hasAnyUpgrades()){
            // Add Upgrade text
            tooltip.add(new TranslatableComponent("TOP.tooltip.cagedmobs.cage.upgrades"));
            // Iterate through upgrades
            List<IElement> upgrades = new ArrayList<>();
            for(ItemStack upgrade : tile.getUpgradesAsItemStacks()){
                if(!upgrade.isEmpty()){
                    upgrades.add(helper.item(upgrade));
                }
            }
            // Render a list of upgrades
            tooltip.add(upgrades);
        }
    }
}
