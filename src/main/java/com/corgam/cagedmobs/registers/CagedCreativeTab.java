package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import net.minecraft.world.item.CreativeModeTab;

import net.minecraft.world.item.ItemStack;

public class CagedCreativeTab extends CreativeModeTab {
    public static final CagedCreativeTab CAGED_MAIN = new CagedCreativeTab(CreativeModeTab.getGroupCountSafe(), CagedMobs.MOD_ID + "tab");

    private CagedCreativeTab(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(CagedItems.HOPPING_MOB_CAGE.get());
    }
}