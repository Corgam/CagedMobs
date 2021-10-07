package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.addons.theoneprobe.ITopInfoProvider;
import com.corgam.cagedmobs.items.*;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.tileEntities.MobCageTE;
import com.corgam.cagedmobs.setup.CagedItems;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class MobCageBlock extends ContainerBlock implements ITopInfoProvider, IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public MobCageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new MobCageTE(false);
    }

    // If placed in water, waterlog it.
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        FluidState fluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return super.getStateForPlacement(p_196258_1_).setValue(WATERLOGGED, flag);
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(WATERLOGGED);
    }

    public BlockState updateShape(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if (p_196271_1_.getValue(WATERLOGGED)) {
            p_196271_4_.getLiquidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickDelay(p_196271_4_));
        }

        return super.updateShape(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }

    public FluidState getFluidState(BlockState p_204507_1_) {
        return p_204507_1_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_204507_1_);
    }

    @Override
    public boolean hasTileEntity (BlockState state) {
        return true;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        // If on client leave the functionality for the server only
        if(worldIn.isClientSide()) {
            return ActionResultType.SUCCESS;
        }
        // Error check for tile entity
        final TileEntity tile = worldIn.getBlockEntity(pos);
        if( tile instanceof MobCageTE) {
            final MobCageTE cage = (MobCageTE) tile;
            final ItemStack heldItem = player.getItemInHand(handIn);
            // Try to remove upgrade, env or mob
            if(player.isCrouching()) {
                if(cage.isCooking() || cage.isLightning() || cage.isArrow()){
                    if(cage.isCooking()){
                        cage.setCooking(false);
                        cage.dropItem(new ItemStack(CagedItems.COOKING_UPGRADE.get(),1));
                        return ActionResultType.SUCCESS;
                    }else if(cage.isLightning()){
                        cage.setLightning(false);
                        cage.dropItem(new ItemStack(CagedItems.LIGHTNING_UPGRADE.get(),1));
                        return ActionResultType.SUCCESS;
                    }else if(cage.isArrow()){
                        cage.setArrow(false);
                        cage.dropItem(new ItemStack(CagedItems.ARROW_UPGRADE.get(),1));
                        return ActionResultType.SUCCESS;
                    }
                }
                if(cage.hasEntity()) {
                    cage.onEntityRemoval();
                    return ActionResultType.SUCCESS;
                }else if(cage.hasEnvironment()){
                    if(cage.hasEnvironment()) {
                        cage.dropItem(cage.getEnvItem().copy());
                    }
                    cage.onEnvironmentRemoval();
                    return ActionResultType.SUCCESS;
                }
            }
            // Try to get back entity
            if(cage.hasEntity()){
                // Restore the entity to the sampler
                if(heldItem.getItem() instanceof DnaSamplerItem) {
                    DnaSamplerItem sampler = (DnaSamplerItem) heldItem.getItem();
                    if(!DnaSamplerItem.containsEntityType(heldItem)){
                        // Check if sampler's tier is sufficient
                        if(cage.getEntity().getSamplerTier() >= 3  && !(heldItem.getItem() instanceof DnaSamplerNetheriteItem)){
                            player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mobcage.samplerNotSufficient").withStyle(TextFormatting.RED), true);
                            return ActionResultType.FAIL;
                        }
                        if(cage.getEntity().getSamplerTier() >= 2  && !((heldItem.getItem() instanceof DnaSamplerNetheriteItem) || (heldItem.getItem() instanceof DnaSamplerDiamondItem))){
                            player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mobcage.samplerNotSufficient").withStyle(TextFormatting.RED), true);
                            return ActionResultType.FAIL;
                        }
                        // Get back the entity
                        sampler.setEntityTypeFromCage(cage, heldItem, player, handIn);
                    }else{
                        player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mobcage.samplerAlreadyUsed").withStyle(TextFormatting.RED), true);
                        return ActionResultType.FAIL;
                    }
                }
                cage.onEntityRemoval();
                return ActionResultType.SUCCESS;
            }
            //Try to add an upgrade
            if(!cage.isCooking() || !cage.isLightning() || !cage.isArrow()){
                if(heldItem.getItem() instanceof UpgradeItem) {
                    if(heldItem.getItem() instanceof CookingUpgradeItem && !cage.isCooking()){
                        cage.setCooking(true);
                        if(!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                        return ActionResultType.SUCCESS;
                    }else if(heldItem.getItem() instanceof LightningUpgradeItem && !cage.isLightning()){
                        cage.setLightning(true);
                        if(!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                        return ActionResultType.SUCCESS;
                    }else if(heldItem.getItem() instanceof ArrowUpgradeItem && !cage.isArrow()){
                        cage.setArrow(true);
                        if(!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                    }
                }
            }
            // Try to add env
            if(!cage.hasEnvironment()) {
                // Check if there exists a recipe with given item
                if(MobCageTE.existsEnvironmentFromItemStack(heldItem)){
                    cage.setEnvironment(heldItem);
                    // Consume one item if not in creative
                    if(!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    return ActionResultType.SUCCESS;
                }else if(heldItem.getItem() instanceof DnaSamplerItem){
                    player.displayClientMessage(new TranslationTextComponent("block.cagedmobs.mobcage.envRequired").withStyle(TextFormatting.RED), true);
                }
                return ActionResultType.PASS;
            }
            // Try to add a mob
            if(!cage.hasEntity()){
                // Check if player holds DNA Sampler
                if(heldItem.getItem() instanceof DnaSamplerItem) {
                    DnaSamplerItem sampler = (DnaSamplerItem) heldItem.getItem();
                    // Check if there exists a recipe with given entity type and if the env suits the entity
                    if(MobCageTE.existsEntityFromType(sampler.getEntityType(heldItem))
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
                        return ActionResultType.SUCCESS;
                    }
                    return ActionResultType.PASS;
                }
                return ActionResultType.PASS;
            }
            // Try to harvest
            if((!cage.isHopping() || CagedMobs.SERVER_CONFIG.ifHoppingCagesDisabled()) && cage.isWaitingForHarvest()) {
                cage.onPlayerHarvest();
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        }
        return ActionResultType.FAIL;
    }

    @Override
    public void onRemove (BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(state.hasTileEntity() && state.getBlock() != newState.getBlock()){
            final TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof MobCageTE) {
                final MobCageTE tile = (MobCageTE) tileEntity;
                if(tile.hasEnvironment()){
                    tile.dropItem(tile.getEnvItem().copy());
                }
                if(tile.isCooking()){
                    tile.dropItem(new ItemStack(CagedItems.COOKING_UPGRADE.get(),1));
                }
                if(tile.isLightning()){
                    tile.dropItem(new ItemStack(CagedItems.LIGHTNING_UPGRADE.get(),1));
                }
                if(tile.isArrow()){
                    tile.dropItem(new ItemStack(CagedItems.ARROW_UPGRADE.get(),1));
                }
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.mainInfo").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.envInfo").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.upgrading").withStyle(TextFormatting.GRAY));
    }

    /// SHAPE ///

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    /// MODS SUPPORT ///

    // Used for TheOneProbe support
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        if(!(world.getBlockEntity(data.getPos()) instanceof MobCageTE)) {
            return;
        }
        MobCageTE tile = (MobCageTE) world.getBlockEntity(data.getPos());
        if(tile != null) {
            if(tile.hasEnvironment() && tile.hasEntity()) {
                probeInfo.progress((int)(tile.getGrowthPercentage()*100), 100, probeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));
            }
            if (tile.hasEnvironment()) {
                probeInfo.horizontal().text(new TranslationTextComponent("HWYLA.tooltip.cagedmobs.cage.environment"));
                probeInfo.horizontal().item(tile.getEnvItem()).itemLabel(tile.getEnvItem());
            }
            if(tile.hasEntity()){
                probeInfo.horizontal().text(new StringTextComponent(
                        new TranslationTextComponent("HWYLA.tooltip.cagedmobs.cage.entity").withStyle(TextFormatting.GRAY).getString() +
                                new TranslationTextComponent(tile.getEntityType().getDescriptionId()).withStyle(TextFormatting.GRAY
                                ).getString()));
            }
            // Upgrades
            if(tile.hasUpgrade()){
                probeInfo.horizontal().text(new TranslationTextComponent("TOP.tooltip.cagedmobs.cage.upgrades"));
            }
            if(tile.isLightning() && tile.isArrow() && tile.isCooking()){
                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.isLightning() && tile.isCooking()){
                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.isLightning() && tile.isArrow()){
                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.isCooking() && tile.isArrow()){
                probeInfo.horizontal().item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.isLightning()){
                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.isCooking()){
               probeInfo.item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance());
            }
            else if(tile.isArrow()){
               probeInfo.horizontal().item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
            }

        }
    }

}
