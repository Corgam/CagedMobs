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
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity.ENVIRONMENT_SLOT;

public class MobCageBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, ITopInfoProvider {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HOPPING = BooleanProperty.create("hopping");
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public MobCageBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(HOPPING, Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED).add(HOPPING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MobCageBlockEntity(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        // If on client side, skip.
        if(level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MobCageBlockEntity cageBE){
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
                    return InteractionResult.SUCCESS;
                }
            }
            // Try to add upgrades
            if(cageBE.acceptsUpgrades()){
                if(heldItem.getItem() instanceof UpgradeItem){
                    cageBE.addUpgrade(heldItem);
                    if(!player.isCreative()){
                        heldItem.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            // Add or remove entity
            if(heldItem.getItem() instanceof DnaSamplerItem sampler){
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
                            return InteractionResult.SUCCESS;
                        }
                        return InteractionResult.FAIL;
                        // Retrieve entity from the cage
                    } else {
                        if (!DnaSamplerItem.containsEntityType(heldItem)) {
                            // Check if sampler's tier is sufficient
                            if (cageBE.getEntity().getSamplerTier() >= 3 && !(heldItem.getItem() instanceof DnaSamplerNetheriteItem)) {
                                player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.samplerNotSufficient").withStyle(ChatFormatting.RED), true);
                                return InteractionResult.FAIL;
                            }
                            if (cageBE.getEntity().getSamplerTier() >= 2 && !((heldItem.getItem() instanceof DnaSamplerNetheriteItem) || (heldItem.getItem() instanceof DnaSamplerDiamondItem))) {
                                player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.samplerNotSufficient").withStyle(ChatFormatting.RED), true);
                                return InteractionResult.FAIL;
                            }
                            // Get back the entity
                            sampler.setEntityTypeFromCage(cageBE, heldItem, player, hand);
                            cageBE.setChanged();
                        } else {
                            player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.cageAlreadyUsed").withStyle(ChatFormatting.RED), true);
                            return InteractionResult.FAIL;
                        }
                        cageBE.removeEntity();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            // Add entity from spawn egg
            if(heldItem.getItem() instanceof SpawnEggItem spawnEggItem){
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
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.cageAlreadyUsed").withStyle(ChatFormatting.RED), true);
                    }
                }else{
                    player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.spawnEggsDisabled").withStyle(ChatFormatting.RED), true);
                }
                return InteractionResult.CONSUME;
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
                            return InteractionResult.SUCCESS;
                        }
                       return InteractionResult.FAIL;
                    }
                }else{
                    player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.spawnEggsDisabled").withStyle(ChatFormatting.RED), true);
                    return InteractionResult.FAIL;
                }
            }
            // Try to harvest the cage with sword
            if(heldItem.getItem() instanceof SwordItem){
                if((!state.getValue(HOPPING) || CagedMobs.SERVER_CONFIG.ifHoppingCagesDisabled()) && cageBE.isWaitingForHarvest()){
                    cageBE.onPlayerHarvest(cageBE.getBlockState());
                    return InteractionResult.SUCCESS;
                }
            }
            // If crouching remove entity
            if(player.isCrouching()){
                if(cageBE.hasEntity()){
                    cageBE.removeEntity();
                    cageBE.setChanged();
                    return InteractionResult.SUCCESS;
                }
            }
            // Open the GUI
            MenuProvider containerProvider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return state.getValue(HOPPING) ? Component.translatable("block.cagedmobs.hoppingmobcage") : Component.translatable("block.cagedmobs.mobcage");
                }
                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                    AbstractContainerMenu menu = new MobCageContainer(pContainerId, pPlayer, pos);
                    menu.addSlotListener(new ContainerListener() {
                        @Override
                        public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack) {
                            if(pDataSlotIndex == ENVIRONMENT_SLOT){
                                cageBE.updateEnvironment();
                            }
                        }
                        @Override
                        public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {}
                    });
                    return menu;
                }
            };
            NetworkHooks.openScreen((ServerPlayer) player, containerProvider, cageBE.getBlockPos());
            return InteractionResult.SUCCESS;
        } else {
        throw new IllegalStateException("Mob Cage container provider is missing!");
        }
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CagedBlockEntities.MOB_CAGE_BLOCK_ENTITY.get(), MobCageBlockEntity::tick);
    }

    /**
     * Called on block remove, should drop all items in the inventory.
     */
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof final MobCageBlockEntity tile) {
                tile.dropInventory();
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    // Block item description

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack item, @javax.annotation.Nullable BlockGetter getter, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("block.cagedmobs.mobcage.mainInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("block.cagedmobs.mobcage.envInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("block.cagedmobs.mobcage.upgrading").withStyle(ChatFormatting.GRAY));
    }

    // Block shape

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // Water-logging

    @javax.annotation.Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(pContext).setValue(WATERLOGGED, Boolean.valueOf(flag));
    }

    /**
     * Called when placed or when neighbour is updated.
     */
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
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
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
        if((world.getBlockEntity(data.getPos()) instanceof MobCageBlockEntity tile)) {
            if (tile.hasEnvironment() && tile.hasEntity()) {
                probeInfo.progress((int) (tile.getGrowthPercentage() * 100), 100, probeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));
            }
            if (tile.hasEnvironment()) {
                probeInfo.horizontal().text(Component.translatable("HWYLA.tooltip.cagedmobs.cage.environment"));
                ItemStack envItem = tile.getInventoryHandler().getStackInSlot(ENVIRONMENT_SLOT);
                if(!envItem.isEmpty()){
                    probeInfo.horizontal().item(envItem).itemLabel(envItem);
                }
            }
            if(tile.hasEntity()){
                probeInfo.horizontal().text(Component.translatable("HWYLA.tooltip.cagedmobs.cage.entity").withStyle(ChatFormatting.GRAY).getString() +
                        Component.translatable(tile.getEntityType().getDescriptionId()).withStyle(ChatFormatting.GRAY).getString());
            }
            // Upgrades
            if(tile.hasAnyUpgrades()){
                // Add Upgrade text
                probeInfo.horizontal().text(Component.translatable("TOP.tooltip.cagedmobs.cage.upgrades"));
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
