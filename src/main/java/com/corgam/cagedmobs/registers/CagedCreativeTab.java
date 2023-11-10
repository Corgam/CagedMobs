package com.corgam.cagedmobs.registers;

import com.corgam.cagedmobs.CagedMobs;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CagedCreativeTab extends ItemGroup {
    public static final CagedCreativeTab CAGED_MAIN = new CagedCreativeTab(ItemGroup.getGroupCountSafe(), CagedMobs.MOD_ID + "_tab");

    private CagedCreativeTab(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(CagedItems.HOPPING_MOB_CAGE.get());
    }
}