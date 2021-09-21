package com.corgam.cagedmobs.setup;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CagedItemGroup extends CreativeModeTab {
    public static final CagedItemGroup CAGED_MAIN = new CagedItemGroup(CreativeModeTab.getGroupCountSafe(), Constants.MOD_ID + "tab");

    private CagedItemGroup(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(CagedItems.DNA_SAMPLER_NETHERITE.get());
    }
}
