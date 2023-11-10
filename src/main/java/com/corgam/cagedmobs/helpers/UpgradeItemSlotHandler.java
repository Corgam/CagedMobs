package com.corgam.cagedmobs.helpers;

import com.corgam.cagedmobs.items.upgrades.UpgradeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class UpgradeItemSlotHandler extends SlotItemHandler {

    public UpgradeItemSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        if (stack.isEmpty()){
            return false;
        }else{
            return stack.getItem() instanceof UpgradeItem;
        }
    }
    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack){
        return 1;
    }
}
