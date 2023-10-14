package com.corgam.cagedmobs.helpers;

import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class EnvironmentItemSlotHandler extends SlotItemHandler {

    public EnvironmentItemSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack)
    {
        if (stack.isEmpty()){
            return false;
        }else{
            return MobCageBlockEntity.existsEnvironmentFromItemStack(stack);
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
