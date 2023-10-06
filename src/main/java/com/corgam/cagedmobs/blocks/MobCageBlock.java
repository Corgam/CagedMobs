package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.addons.theoneprobe.ITopInfoProvider;
import com.corgam.cagedmobs.blockEntities.MobCageBlockEntity;
import com.corgam.cagedmobs.items.*;
import com.corgam.cagedmobs.items.upgrades.ArrowUpgradeItem;
import com.corgam.cagedmobs.items.upgrades.CookingUpgradeItem;
import com.corgam.cagedmobs.items.upgrades.LightningUpgradeItem;
import com.corgam.cagedmobs.items.upgrades.UpgradeItem;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.registers.CagedBlockEntity;
import com.corgam.cagedmobs.registers.CagedItems;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class MobCageBlock extends BaseEntityBlock implements ITopInfoProvider, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public MobCageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MobCageBlockEntity(pos, state, false);
    }


    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CagedBlockEntity.MOB_CAGE.get(), MobCageBlockEntity::tick);
    }

    // If placed in water, waterlog the cage.
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidstate = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(placeContext).setValue(WATERLOGGED, flag);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public BlockState updateShape(BlockState stateOld, Direction dir, BlockState stateNew, LevelAccessor accessor, BlockPos posOld, BlockPos posNew) {
        if (stateOld.getValue(WATERLOGGED)) {
            accessor.scheduleTick(posOld, Fluids.WATER, Fluids.WATER.getTickDelay(accessor));
        }
        return super.updateShape(stateOld, dir, stateNew, accessor, posOld, posNew);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        // If on client leave the functionality for the server only
        if(worldIn.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // Error check for tile entity
        final BlockEntity tile = worldIn.getBlockEntity(pos);
        if(tile instanceof final MobCageBlockEntity cage) {
            final ItemStack heldItem = player.getItemInHand(handIn);
            // Try to remove the environment or the entity
            if(player.isCrouching()) {
                if(cage.hasUpgrades()){
                    if(cage.hasCookingUpgrades()){
                        cage.setCooking(false);
                        cage.dropItem(new ItemStack(CagedItems.COOKING_UPGRADE.get(),1));
                        cage.setChanged();
                        return InteractionResult.SUCCESS;
                    }else if(cage.hasLightningUpgrades()){
                        cage.setLightning(false);
                        cage.dropItem(new ItemStack(CagedItems.LIGHTNING_UPGRADE.get(),1));
                        cage.setChanged();
                        return InteractionResult.SUCCESS;
                    }else if(cage.hasArrowsUpgrades()){
                        cage.setArrow(false);
                        cage.dropItem(new ItemStack(CagedItems.ARROW_UPGRADE.get(),1));
                        cage.setChanged();
                        return InteractionResult.SUCCESS;
                    }
                }
                if(cage.hasEntity()) {
                    cage.onEntityRemoval();
                    return InteractionResult.SUCCESS;
                }else if(cage.hasEnvironment()){
                    if(cage.hasEnvironment()) {
                        cage.dropItem(cage.getEnvItem().copy());
                    }
                    cage.onEnvironmentRemoval();
                    return InteractionResult.SUCCESS;
                }
            }
            // Try to get back entity
            if(cage.hasEntity()){
                // Restore the entity to the sampler
                if(heldItem.getItem() instanceof DnaSamplerItem sampler) {
                    if(!DnaSamplerItem.containsEntityType(heldItem)){
                        // Check if sampler's tier is sufficient
                        if(cage.getEntity().getSamplerTier() >= 3  && !(heldItem.getItem() instanceof DnaSamplerNetheriteItem)){
                            player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.samplerNotSufficient").withStyle(ChatFormatting.RED), true);
                            return InteractionResult.FAIL;
                        }
                        if(cage.getEntity().getSamplerTier() >= 2  && !((heldItem.getItem() instanceof DnaSamplerNetheriteItem) || (heldItem.getItem() instanceof DnaSamplerDiamondItem))){
                            player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.samplerNotSufficient").withStyle(ChatFormatting.RED), true);
                            return InteractionResult.FAIL;
                        }
                        // Get back the entity
                        sampler.setEntityTypeFromCage(cage, heldItem, player, handIn);
                        cage.setChanged();
                    }else{
                        player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.samplerAlreadyUsed").withStyle(ChatFormatting.RED), true);
                        return InteractionResult.FAIL;
                    }
                    cage.onEntityRemoval();
                    return InteractionResult.SUCCESS;
                }
            }
            //Try to add an upgrade
            if(!cage.hasCookingUpgrades() || !cage.hasLightningUpgrades() || !cage.hasArrowsUpgrades()) {
                if (heldItem.getItem() instanceof UpgradeItem) {
                    if (heldItem.getItem() instanceof CookingUpgradeItem && !cage.hasCookingUpgrades()) {
                        cage.setCooking(true);
                        cage.setChanged();
                        if (!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    } else if (heldItem.getItem() instanceof LightningUpgradeItem && !cage.hasLightningUpgrades()) {
                        cage.setLightning(true);
                        cage.setChanged();
                        if (!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    } else if (heldItem.getItem() instanceof ArrowUpgradeItem && !cage.hasArrowsUpgrades()) {
                        cage.setArrow(true);
                        cage.setChanged();
                        if (!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                    }
                }
            }
            // Try to add env
            if(!cage.hasEnvironment()) {
                // Check if there exists a recipe with given item
                if(MobCageBlockEntity.existsEnvironmentFromItemStack(heldItem)){
                    cage.setEnvironment(heldItem);
                    // Consume one item if not in creative
                    if(!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }else if(heldItem.getItem() instanceof DnaSamplerItem){
                    player.displayClientMessage(Component.translatable("block.cagedmobs.mobcage.envRequired").withStyle(ChatFormatting.RED), true);
                }
                return InteractionResult.PASS;
            }
            // Try to add a mob
            if(!cage.hasEntity()){
                // Check if player holds DNA Sampler
                if(heldItem.getItem() instanceof DnaSamplerItem sampler) {
                    // Check if there exists a recipe with given entity type and if the env suits the entity
                    if(MobCageBlockEntity.existsEntityFromType(sampler.getEntityType(heldItem))
                            && cage.isEnvSuitable(player, sampler.getEntityType(heldItem), state)
                            && !RecipesHelper.isEntityTypeBlacklisted(sampler.getEntityType(heldItem))){
                        cage.setEntityFromType(sampler.getEntityType(heldItem),heldItem);

                        // Clear the sampler's mob type if not in creative
                        if(!player.isCreative()) {
                            // If single use samplers config is enabled, destroy the sampler. If not, just use its DNA.
                            if(CagedMobs.SERVER_CONFIG.areSamplersSingleUse()){
                                player.broadcastBreakEvent(handIn);
                                heldItem.shrink(1);
                            }else{
                                sampler.removeEntityType(heldItem);
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
                return InteractionResult.PASS;
            }
            // Try to harvest
            if((!cage.isHopping() || CagedMobs.SERVER_CONFIG.ifHoppingCagesDisabled()) && cage.isWaitingForHarvest()) {
                cage.onPlayerHarvest();
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onRemove (BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(state.hasBlockEntity() && state.getBlock() != newState.getBlock()){
            final BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof final MobCageBlockEntity tile) {
                if(tile.hasEnvironment()){
                    tile.dropItem(tile.getEnvItem().copy());
                }
                if(tile.hasCookingUpgrades()){
                    tile.dropItem(new ItemStack(CagedItems.COOKING_UPGRADE.get(),1));
                }
                if(tile.hasLightningUpgrades()){
                    tile.dropItem(new ItemStack(CagedItems.LIGHTNING_UPGRADE.get(),1));
                }
                if(tile.hasArrowsUpgrades()){
                    tile.dropItem(new ItemStack(CagedItems.ARROW_UPGRADE.get(),1));
                }
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack item, @Nullable BlockGetter getter, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("block.cagedmobs.mobcage.mainInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("block.cagedmobs.mobcage.envInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("block.cagedmobs.mobcage.upgrading").withStyle(ChatFormatting.GRAY));
    }

    /// SHAPE methods ///

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /// MODS SUPPORT ///

    // Used for TheOneProbe support
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
        if(!(world.getBlockEntity(data.getPos()) instanceof MobCageBlockEntity tile)) {
            return;
        }
        if(tile != null) {
            if(tile.hasEnvironment() && tile.hasEntity()) {
                probeInfo.progress((int)(tile.getGrowthPercentage()*100), 100, probeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));
            }
            if (tile.hasEnvironment()) {
                probeInfo.horizontal().text(Component.translatable("HWYLA.tooltip.cagedmobs.cage.environment"));
                probeInfo.horizontal().item(tile.getEnvItem()).itemLabel(tile.getEnvItem());
            }
            if(tile.hasEntity()){
                probeInfo.horizontal().text(Component.translatable("HWYLA.tooltip.cagedmobs.cage.entity").withStyle(ChatFormatting.GRAY).getString() +
                                Component.translatable(tile.getEntityType().getDescriptionId()).withStyle(ChatFormatting.GRAY
                                ).getString());
            }
            // Upgrades
            if(tile.hasUpgrades()){
                probeInfo.horizontal().text(Component.translatable("TOP.tooltip.cagedmobs.cage.upgrades"));
            }
            if(tile.hasLightningUpgrades() && tile.hasArrowsUpgrades() && tile.hasCookingUpgrades()){
                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.hasLightningUpgrades() && tile.hasCookingUpgrades()){
                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.hasLightningUpgrades() && tile.hasArrowsUpgrades()){
                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.hasCookingUpgrades() && tile.hasArrowsUpgrades()){
                probeInfo.horizontal().item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.hasLightningUpgrades()){
                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.hasCookingUpgrades()){
               probeInfo.item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.hasArrowsUpgrades()){
               probeInfo.horizontal().item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
            }

        }
    }

}
