package com.corgam.cagedmobs.items;

import net.minecraft.item.ItemStack;

public class DnaSamplerNetheriteItem extends DnaSamplerItem{
    public DnaSamplerNetheriteItem(Properties properties) {
        super(properties);
    }

    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}
