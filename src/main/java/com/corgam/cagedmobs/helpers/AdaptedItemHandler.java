package com.corgam.cagedmobs.helpers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * Written by McJty (https://www.mcjty.eu/docs/1.20/).
 */
public class AdaptedItemHandler implements IItemHandlerModifiable {

    private final IItemHandlerModifiable handler;

    public AdaptedItemHandler(IItemHandlerModifiable handler) {
        this.handler = handler;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return handler.isItemValid(slot, stack);
    }
}