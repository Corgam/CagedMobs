package com.corgam.cagedmobs.addons.jade;

import com.corgam.cagedmobs.blockEntities.MobCageBlockEntity;
import com.corgam.cagedmobs.setup.CagedItems;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CagedMobsComponentProvider implements IComponentProvider {

    public static final ProgressStyle PROGRESS_STYLE = new ProgressStyle();
    public static final BorderStyle BORDER_STYLE = new BorderStyle();

    static {
        BORDER_STYLE.width(1);
        PROGRESS_STYLE.color(0xff44AA44, 0xff44AA44); // bg color 0xff836953
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
        if(!(blockAccessor.getBlockEntity() instanceof MobCageBlockEntity)){
            return;
        }
        MobCageBlockEntity tile = (MobCageBlockEntity) blockAccessor.getBlockEntity();
        // Add growth progress
        if(tile.hasEntity() && tile.hasEnvironment()){
            tooltip.add(new ProgressElement(
                    tile.getGrowthPercentage(),
                    new TextComponent(String.format("%3.0f%%", tile.getGrowthPercentage()*100)),
                    PROGRESS_STYLE,
                    BORDER_STYLE
            ));
        }
        // Add env
        if(tile.hasEnvironment()){
            ItemStack representation = tile.getEnvItem();
            if(representation != null){
                tooltip.add(new TranslatableComponent("HWYLA.tooltip.cagedmobs.cage.environment"));
                tooltip.add(List.of(
                        ItemStackElement.of(representation, 1.0F),
                        new TextElement(representation.getHoverName())));
            }
        }
        // Add entity
        if(tile.hasEntity()){
            EntityType<?> representation = tile.getEntityType();
            if(representation != null){
                tooltip.add(new TextComponent(
                        new TranslatableComponent("HWYLA.tooltip.cagedmobs.cage.entity").withStyle(ChatFormatting.GRAY).getString() +
                                new TranslatableComponent(representation.getDescriptionId()).withStyle(ChatFormatting.GRAY).getString()));
            }
        }
        // Add upgrades
        if(tile.hasUpgrade()){
            tooltip.add(new TranslatableComponent("TOP.tooltip.cagedmobs.cage.upgrades"));
            List<IElement> upgrades = new ArrayList<>();
            if(tile.isLightning()){
                upgrades.add(ItemStackElement.of(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance(), 1.0F));
            }
            if(tile.isCooking()){
                upgrades.add(ItemStackElement.of(CagedItems.COOKING_UPGRADE.get().getDefaultInstance(), 1.0F));
            }
            if(tile.isArrow()){
                upgrades.add(ItemStackElement.of(CagedItems.ARROW_UPGRADE.get().getDefaultInstance(), 1.0F));
            }
            tooltip.add(upgrades);
        }
    }
}
