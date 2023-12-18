package com.corgam.cagedmobs.helpers;

import com.corgam.cagedmobs.items.upgrades.UpgradeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class UpgradeItemSlotHandler extends SlotItemHandler {

    public UpgradeItemSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack)
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
    public int getMaxStackSize(@NotNull ItemStack stack){
        return 1;
    }
}
