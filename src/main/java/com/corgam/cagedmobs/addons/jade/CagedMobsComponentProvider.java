package com.corgam.cagedmobs.addons.jade;

import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import com.corgam.cagedmobs.registers.CagedBlocks;
import com.corgam.cagedmobs.setup.Constants;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IBoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.ArrayList;
import java.util.List;

public class CagedMobsComponentProvider implements IBlockComponentProvider {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if(!(blockAccessor.getBlockEntity() instanceof MobCageBlockEntity tile)){
            return;
        }
        IElementHelper helper = tooltip.getElementHelper();
        // Add growth progress
        if(tile.hasEntity() && tile.hasEnvironment()){
            tooltip.add(helper.progress(tile.getGrowthPercentage(),
                    Component.literal(String.format("%3.0f%%", tile.getGrowthPercentage() * 100)),
                    helper.progressStyle().color(0xff44AA44, 0xff44AA44),
                    IBoxStyle.Empty.INSTANCE,
                    true
            ));
        }
        // Add Environment
        if(tile.hasEnvironment()){
            ItemStack representation = tile.getEnvironmentItemStack();
            if(representation != null){
                tooltip.add(Component.translatable("JADE.tooltip.cagedmobs.cage.environment"));
                tooltip.add(List.of(
                        helper.item(representation, 1.0F),
                        helper.text(representation.getHoverName())));
            }
        }
        // Add Entity
        if(tile.hasEntity()){
            EntityType<?> representation = tile.getEntityType();
            if(representation != null){
                tooltip.add(Component.literal(
                        Component.translatable("JADE.tooltip.cagedmobs.cage.entity").withStyle(ChatFormatting.GRAY).getString() +
                                Component.translatable(representation.getDescriptionId()).withStyle(ChatFormatting.GRAY).getString()));
            }
        }
        // Add Upgrades
        if(tile.hasAnyUpgrades()){
            // Add Upgrade text
            tooltip.add(Component.translatable("TOP.tooltip.cagedmobs.cage.upgrades"));
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

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Constants.MOD_ID, "cagedmobs_jade");
    }
}
