package com.corgam.cagedmobs.blockEntities;

import com.corgam.cagedmobs.helpers.AdaptedItemHandler;
import com.corgam.cagedmobs.items.upgrades.UpgradeItem;
import com.corgam.cagedmobs.registers.CagedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TestEntity extends BlockEntity {

    // Item capability
    public static int UPGRADES_COUNT = 3;
    public static int ENVIRONMENT_SLOT = 0;
    public static int SLOT_COUNT = UPGRADES_COUNT + 1;

    private final ItemStackHandler items = createItemHandler();
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> items);
    private final LazyOptional<IItemHandler> restrictedItemHandler = LazyOptional.of(() -> new AdaptedItemHandler(items){
        /**
         * Does not allow item extraction (for example hoppers).
         * @param slot     Slot to extract from.
         * @param amount   Amount to extract
         * @param simulate If true, the extraction is only simulated
         * @return empty item stack
         */
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        /**
         * Does not allow item insertion (for example hoppers).
         * @param slot     Slot to insert into.
         * @param stack    ItemStack to insert. This must not be modified by the item handler.
         * @param simulate If true, the insertion is only simulated
         * @return the same input stack
         */
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    });

    // Saving and loading
    public static final String ITEMS_TAG = "Inventory";

    /**
     * Creates a new cage block entity
     * @param pPos the position of the entity
     * @param pBlockState the state of the entity
     */
    public TestEntity(BlockPos pPos, BlockState pBlockState) {
        super(CagedBlockEntity.TEST_ENTITY.get(), pPos, pBlockState);
    }

    /**
     * Creates the item handler.
     * @return the item handler.
     */
    @Nonnull
    private ItemStackHandler createItemHandler(){
        return new ItemStackHandler(SLOT_COUNT){
            @Override
            protected void onContentsChanged(int slot){
                setChanged();
                if(level != null){
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };
    }

    /**
     * Invalidates the item capability when block is destroyed.
     */
    @Override
    public void invalidateCaps(){
        super.invalidateCaps();
        itemHandler.invalidate();
        restrictedItemHandler.invalidate();
    }

    /**
     * Returns the item handler for environment and upgrades
     * @return items handler
     */
    public ItemStackHandler getInventoryHandler(){
        return this.items;
    }

    /**
     * Returns the item handler capabilities.
     * @param cap The capability to check
     * @param side The Side to check from,
     *   <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
     * @return the item capability
     */
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            if (side == null){
                return itemHandler.cast();
            }else {
                // Return restricted item handler when any side is accessed.
                return restrictedItemHandler.cast();
            }
        }else{
            return super.getCapability(cap, side);
        }
    }

    // SAVING AND LOADING

    /**
     * Saves all the additional tags of the block entity.
     * @param tag the nbt tag to save to
     */
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        super.saveAdditional(tag);
        saveClientData(tag);
    }

    /**
     * Saves custom parameters of the entity  in correct order.
     * Used by the update tag to keep the network overhead small.
     * @param tag the nbt tag to save to
     */
    private void saveClientData(CompoundTag tag){
        // Item capability
        tag.put(ITEMS_TAG, items.serializeNBT());
    }

    /**
     * Loads all the additional tags of the block entity.
     * @param tag the nbt tag to load from
     */
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        loadClientData(tag);
    }

    /**
     * Loads custom parameters of the entity in correct order.
     * Used by the update tag to keep the network overhead small.
     * @param tag the nbt tag to load from
     */
    private void loadClientData(CompoundTag tag) {
        // Item capability
        if (tag.contains(ITEMS_TAG)) {
            items.deserializeNBT(tag.getCompound(ITEMS_TAG));
        }
    }

    // CLIENT-SERVER SYNCHRONIZATION

    /**
     * Creates the update tag on server side, when a new chunk is loaded.
     * @return the update nbt tag to send
     */
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveClientData(tag);
        return tag;
    }

    /**
     * Reads the update tag on client side, when a new chunk is loaded.
     * @param tag The {@link CompoundTag} sent from {@link BlockEntity#getUpdateTag()}
     */
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag != null) {
            loadClientData(tag);
        }
    }

    /**
     * Creates an update packet when the block state has changed.
     * @return the update packet
     */
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        // Uses getUpdateTag() under the hood
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * Loads the received data packet on client side, when the block state has changed.
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        // This will call loadClientData()
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }

    public void tickServer() {
    }
}
