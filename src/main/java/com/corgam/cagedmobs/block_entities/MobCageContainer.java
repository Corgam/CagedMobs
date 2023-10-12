package com.corgam.cagedmobs.block_entities;

import com.corgam.cagedmobs.helpers.EnvironmentItemSlotHandler;
import com.corgam.cagedmobs.helpers.UpgradeItemSlotHandler;
import com.corgam.cagedmobs.registers.CagedBlocks;
import com.corgam.cagedmobs.registers.CagedContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


import java.util.ArrayList;

import static com.corgam.cagedmobs.block_entities.MobCageBlockEntity.ENVIRONMENT_SLOT;
import static com.corgam.cagedmobs.block_entities.MobCageBlockEntity.SLOT_COUNT;

public class MobCageContainer extends AbstractContainerMenu {

    private final BlockPos pos;
    private Slot environmentSlot = null;

    private final ArrayList<Slot> upgradeSlots = new ArrayList<>();

    /**
     * Creates the cage container
     * @param pWindowId the id of the window
     * @param pPlayer the accessing player
     * @param pPos block position
     */
    public MobCageContainer(int pWindowId, Player pPlayer, BlockPos pPos) {
        super(CagedContainers.CAGE_CONTAINER.get(), pWindowId);
        this.pos = pPos;
        if(pPlayer.level().getBlockEntity(pos) instanceof MobCageBlockEntity cage){
            // Environment
            this.environmentSlot = addSlot(new EnvironmentItemSlotHandler(cage.getInventoryHandler(), ENVIRONMENT_SLOT, 26, 44));
            // Upgrades
            upgradeSlots.add(addSlot(new UpgradeItemSlotHandler(cage.getInventoryHandler(), ENVIRONMENT_SLOT + 1, 134, 23)));
            upgradeSlots.add(addSlot(new UpgradeItemSlotHandler(cage.getInventoryHandler(), ENVIRONMENT_SLOT + 2, 134, 44)));
            upgradeSlots.add(addSlot(new UpgradeItemSlotHandler(cage.getInventoryHandler(), ENVIRONMENT_SLOT + 3, 134, 65)));
        }
        layoutPlayerInventorySlots(pPlayer.getInventory(), 8, 101);
    }

    private void layoutPlayerInventorySlots(Inventory inventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18);
        // Player Hot-bar
        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18);
    }

    /**
     * Adds a box of inventory slots.
     * Written by McJty (https://www.mcjty.eu/docs/1.20/).
     */
    private int addSlotBox(Container playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    /**
     * Adds a range of inventory slots.
     * Written by McJty (https://www.mcjty.eu/docs/1.20/).
     */
    private int addSlotRange(Container playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    /**
     * Used for logic of shift-clicking items.
     * @param pPlayer player accessing the block
     * @param pIndex slot index
     * @return item stack
     */
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            // Get items from cage to the inventory
            if (pIndex < SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, SLOT_COUNT, Inventory.INVENTORY_SIZE + SLOT_COUNT, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (!this.moveItemStackTo(stack, 0, SLOT_COUNT+1, false)) {
                if (pIndex < 27 + SLOT_COUNT) {
                    if (!this.moveItemStackTo(stack, 27 + SLOT_COUNT, 36 + SLOT_COUNT, false)) {
                        this.broadcastFullState();
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex < Inventory.INVENTORY_SIZE + SLOT_COUNT && !this.moveItemStackTo(stack, SLOT_COUNT, 27 + SLOT_COUNT, false)) {
                    slot.setChanged();
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(pPlayer, stack);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(pPlayer.level(), pos), pPlayer, CagedBlocks.MOB_CAGE.get())
                || stillValid(ContainerLevelAccess.create(pPlayer.level(), pos), pPlayer, CagedBlocks.HOPPING_MOB_CAGE.get());
    }

    public Slot getEnvironmentSlot() {
        return environmentSlot;
    }

    public ArrayList<Slot> getUpgradeSlots() {
        return upgradeSlots;
    }
}
