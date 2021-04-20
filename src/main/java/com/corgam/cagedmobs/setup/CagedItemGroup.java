package com.corgam.cagedmobs.setup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CagedItemGroup extends ItemGroup {
    public static final CagedItemGroup CAGED_MAIN = new CagedItemGroup(ItemGroup.getGroupCountSafe(), Constants.MOD_ID + "tab");

    private CagedItemGroup(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(CagedItems.HOPPING_MOB_CAGE.get());
    }
}
