package com.corgam.cagedmobs.blocks.mob_cage;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.addons.theoneprobe.ITopInfoProvider;
import com.corgam.cagedmobs.items.DnaSamplerDiamondItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.DnaSamplerNetheriteItem;
import com.corgam.cagedmobs.items.EmptySpawnEggItem;
import com.corgam.cagedmobs.items.upgrades.UpgradeItem;
import com.corgam.cagedmobs.registers.CagedBlockEntities;
import com.corgam.cagedmobs.registers.CagedItems;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import java.util.List;

import static com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity.ENVIRONMENT_SLOT;

public class MobCageBlock extends ContainerBlock implements IWaterLoggable, ITopInfoProvider {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HOPPING = BooleanProperty.create("hopping");
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public MobCageBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(HOPPING, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED).add(HOPPING);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new MobCageBlockEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        // If on client side, skip.
        if(level.isClientSide()) {
            return ActionResultType.SUCCESS;
        }
        TileEntity be = level.getBlockEntity(pos);
        if (be instanceof MobCageBlockEntity){
            MobCageBlockEntity cageBE = (MobCageBlockEntity) be;
            // Get item in hand
            final ItemStack heldItem = player.getItemInHand(hand);
            // Try to add environment
            if(!cageBE.hasEnvironment()){
                // Check if there exists a recipe for given item
                if(cageBE.existsEnvironmentFromItemStack(heldItem)){
                    // Set environment
                    cageBE.setEnvironment(heldItem);
                    if(!player.isCreative()){
                        heldItem.shrink(1);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
            // Try to add upgrades
            if(cageBE.acceptsUpgrades()){
                if(heldItem.getItem() instanceof UpgradeItem){
                    cageBE.addUpgrade(heldItem);
                    if(!player.isCreative()){
                        heldItem.shrink(1);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
            // Add or remove entity
            if(heldItem.getItem() instanceof DnaSamplerItem){
                DnaSamplerItem sampler = (DnaSamplerItem) heldItem.getItem();
                if(!CagedMobs.SERVER_CONFIG.areSamplersDisabled()) {
                    if (!cageBE.hasEntity()) {
                        // Check if there exists a recipe for a given entity type,
                        // if the environment is suitable for that entity and if it is not blacklisted.
                        if (cageBE.existsEntityDataFromType(sampler.getEntityType(heldItem))
                                && cageBE.isEnvironmentSuitable(player, sampler.getEntityType(heldItem), state)
                                && !RecipesHelper.isEntityTypeBlacklisted(sampler.getEntityType(heldItem))) {
                            // Add entity
                            cageBE.setEntityFromSampler(sampler.getEntityType(heldItem), heldItem);
                            // Clear the sampler
                            if (!player.isCreative()) {
                                // If single use samplers config is enabled, destroy the sampler. If not, just use its DNA.
                                if (CagedMobs.SERVER_CONFIG.areSamplersSingleUse()) {
                                    player.broadcastBreakEvent(hand);
                                    heldItem.shrink(1);
                                } else {
                                    sampler.removeEntityType(heldItem);
                                }
                            }
                            return ActionResultType.SUCCESS;
                        }
                        return ActionResultType.FAIL;
                    // Retrieve entity from the cage
                    } else {
                        if (!DnaSamplerItem.containsEntityType(heldItem) && cageBE.getEntity().isPresent()) {
                            // Check if sampler's tier is sufficient
                            if (cageBE.getEntity().get().getSamplerTier() >= 3 && !(heldItem.getItem() instanceof DnaSamplerNetheriteItem)) {
                                player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mob_cage.samplerNotSufficient").withStyle(TextFormatting.RED), true);
                                return ActionResultType.FAIL;
                            }
                            if (cageBE.getEntity().get().getSamplerTier() >= 2 && !((heldItem.getItem() instanceof DnaSamplerNetheriteItem) || (heldItem.getItem() instanceof DnaSamplerDiamondItem))) {
                                player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mob_cage.samplerNotSufficient").withStyle(TextFormatting.RED), true);
                                return ActionResultType.FAIL;
                            }
                            // Get back the entity
                            sampler.setEntityTypeFromCage(cageBE, heldItem, player, hand);
                            cageBE.setChanged();
                        } else {
                            player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mob_cage.cageAlreadyUsed").withStyle(TextFormatting.RED), true);
                            return ActionResultType.FAIL;
                        }
                        cageBE.removeEntity();
                        return ActionResultType.SUCCESS;
                    }
                }
            }
            // Add entity from spawn egg
            if(heldItem.getItem() instanceof SpawnEggItem){
                SpawnEggItem spawnEggItem = (SpawnEggItem) heldItem.getItem();
                if(!CagedMobs.SERVER_CONFIG.areSpawnEggsDisabled()) {
                    if (!cageBE.hasEntity()) {
                        EntityType<?> entityType = spawnEggItem.getType(heldItem.getOrCreateTag());
                        // Check if there exists a recipe for a given entity type,
                        // if the environment is suitable for that entity and if it is not blacklisted.
                        if (cageBE.existsEntityDataFromType(entityType)
                                && cageBE.isEnvironmentSuitable(player, entityType, state)
                                && !RecipesHelper.isEntityTypeBlacklisted(entityType)) {
                            // Add entity
                            cageBE.setEntityFromSampler(entityType, heldItem);
                            // Clear the sampler
                            if (!player.isCreative()) {
                                heldItem.shrink(1);
                                player.addItem(new ItemStack(CagedItems.EMPTY_SPAWN_EGG.get()));
                            }
                            return ActionResultType.SUCCESS;
                        }
                    } else {
                        player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mob_cage.cageAlreadyUsed").withStyle(TextFormatting.RED), true);
                    }
                }else{
                    player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mob_cage.spawnEggsDisabled").withStyle(TextFormatting.RED), true);
                }
                return ActionResultType.CONSUME;
            }
            // Retrieve entity from the cage with empty spawn egg
            if(heldItem.getItem() instanceof EmptySpawnEggItem){
                if(!CagedMobs.SERVER_CONFIG.areSpawnEggsDisabled()){
                    if(cageBE.hasEntity()){
                        SpawnEggItem spawnEgg = ForgeSpawnEggItem.fromEntityType(cageBE.getEntityType());
                        if(spawnEgg != null){
                            if(!player.isCreative()){
                                player.addItem(new ItemStack(spawnEgg));
                                heldItem.shrink(1);
                            }
                            cageBE.removeEntity();
                            return ActionResultType.SUCCESS;
                        }
                       return ActionResultType.FAIL;
                    }
                }else{
                    player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mob_cage.spawnEggsDisabled").withStyle(TextFormatting.RED), true);
                    return ActionResultType.FAIL;
                }
            }
            // Try to harvest the cage with sword
            if(heldItem.getItem() instanceof SwordItem){
                if((!state.getValue(HOPPING) || CagedMobs.SERVER_CONFIG.ifHoppingCagesDisabled()) && cageBE.isWaitingForHarvest()){
                    cageBE.onPlayerHarvest(cageBE.getBlockState());
                    if(!player.isCreative() && !level.isClientSide()){
                        heldItem.hurt(1, level.random, null);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
            // If crouching remove entity
            if(player.isCrouching()){
                if(cageBE.hasEntity()){
                    cageBE.removeEntity();
                    cageBE.setChanged();
                    return ActionResultType.SUCCESS;
                }
            }
            // Open the GUI
            INamedContainerProvider containerProvider = new INamedContainerProvider() {
                @Nullable
                @Override
                public Container createMenu(int pContainerId, PlayerInventory pPlayerInventory, PlayerEntity pPlayer) {
                    Container menu = new MobCageContainer(pContainerId, pPlayer, pos);
                    menu.addSlotListener(new IContainerListener() {
                        @Override
                        public void refreshContainer(Container pContainerToSend, NonNullList<ItemStack> pItemsList) {

                        }

                        @Override
                        public void slotChanged(Container pContainerToSend, int pDataSlotIndex, ItemStack pStack) {
                            if(pDataSlotIndex == ENVIRONMENT_SLOT){
                                cageBE.updateEnvironment();
                            }
                        }

                        @Override
                        public void setContainerData(Container pContainer, int pVarToUpdate, int pNewValue) {

                        }
                    });
                    return menu;
                }

                @Override
                public ITextComponent getDisplayName() {
                    return state.getValue(HOPPING) ? new TranslationTextComponent("block.cagedmobs.hopping_mob_cage") : new TranslationTextComponent("block.cagedmobs.mob_cage");
                }
            };
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, cageBE.getBlockPos());
            return ActionResultType.SUCCESS;
        } else {
        throw new IllegalStateException("Mob Cage container provider is missing!");
        }
    }

//    @Nullable
//    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, TileEntityType<T> type) {
//        return createTickerHelper(type, CagedBlockEntities.MOB_CAGE_BLOCK_ENTITY.get(), MobCageBlockEntity::tick);
//    }

    /**
     * Called on block remove, should drop all items in the inventory.
     */
    @Override
    public void onRemove(BlockState pState, World pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            TileEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof MobCageBlockEntity) {
                final MobCageBlockEntity tile = (MobCageBlockEntity) blockentity;
                tile.dropInventory();
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    // Block item description
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable IBlockReader pLevel, List<ITextComponent> tooltip, ITooltipFlag pFlag) {
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mob_cage.mainInfo").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mob_cage.rightClickHarvest").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mob_cage.envInfo").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mob_cage.upgrading").withStyle(TextFormatting.GRAY));
    }

    // Block shape
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    // Water-logging
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(pContext).setValue(WATERLOGGED, Boolean.valueOf(flag));
    }

    /**
     * Called when placed or when neighbour is updated.
     */
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, IWorld pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.getLiquidTicks().scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    // Mods Support

    /**
     * Used for TheOneProbe mod support.
     */
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        if((world.getBlockEntity(data.getPos()) instanceof MobCageBlockEntity)) {
            MobCageBlockEntity tile = (MobCageBlockEntity) world.getBlockEntity(data.getPos());
            if (tile.hasEnvironment() && tile.hasEntity()) {
                probeInfo.progress((int) (tile.getGrowthPercentage() * 100), 100, probeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));
            }
            if (tile.hasEnvironment()) {
                probeInfo.horizontal().text(new TranslationTextComponent("JADE.tooltip.cagedmobs.cage.environment"));
                ItemStack envItem = tile.getInventoryHandler().getStackInSlot(ENVIRONMENT_SLOT);
                if(!envItem.isEmpty()){
                    probeInfo.horizontal().item(envItem).itemLabel(envItem);
                }
            }
            if(tile.hasEntity()){
                probeInfo.horizontal().text(new TranslationTextComponent("JADE.tooltip.cagedmobs.cage.entity").withStyle(TextFormatting.GRAY).getString() +
                        new TranslationTextComponent(tile.getEntityType().getDescriptionId()).withStyle(TextFormatting.GRAY).getString());
            }
            // Upgrades
            if(tile.hasAnyUpgrades()){
                // Add Upgrade text
                probeInfo.horizontal().text(new TranslationTextComponent("TOP.tooltip.cagedmobs.cage.upgrades"));
                IProbeInfo hor = probeInfo.horizontal();
                for(ItemStack upgrade : tile.getUpgradesAsItemStacks()){
                    if(!upgrade.isEmpty()){
                        hor.item(upgrade);
                    }
                }
            }
        }
    }
}
