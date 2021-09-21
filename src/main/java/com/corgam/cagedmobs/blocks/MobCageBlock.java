package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.items.*;
import com.corgam.cagedmobs.tileEntities.MobCageTE;
import com.corgam.cagedmobs.setup.CagedItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class MobCageBlock extends BaseEntityBlock /*implements ITopInfoProvider */{
    private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public MobCageBlock(Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MobCageTE(false);
    }

//    @Override
//    public boolean hasTileEntity (BlockState state) {
//        return true;
//    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        // If on client leave the functionality for the server only
        if(worldIn.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        // Error check for tile entity
        final BlockEntity tile = worldIn.getBlockEntity(pos);
        if( tile instanceof MobCageTE) {
            final MobCageTE cage = (MobCageTE) tile;
            final ItemStack heldItem = player.getItemInHand(handIn);
            // Try to remove upgrade, env or mob
            if(player.isCrouching()) {
                if(cage.isCooking() || cage.isLightning() || cage.isArrow()){
                    if(cage.isCooking()){
                        cage.setCooking(false);
                        cage.dropItem(new ItemStack(CagedItems.COOKING_UPGRADE.get(),1));
                        return InteractionResult.SUCCESS;
                    }else if(cage.isLightning()){
                        cage.setLightning(false);
                        cage.dropItem(new ItemStack(CagedItems.LIGHTNING_UPGRADE.get(),1));
                        return InteractionResult.SUCCESS;
                    }else if(cage.isArrow()){
                        cage.setArrow(false);
                        cage.dropItem(new ItemStack(CagedItems.ARROW_UPGRADE.get(),1));
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
            //Try to add an upgrade
            if(!cage.isCooking() || !cage.isLightning() || !cage.isArrow()){
                if(heldItem.getItem() instanceof UpgradeItem) {
                    if(heldItem.getItem() instanceof CookingUpgradeItem && !cage.isCooking()){
                        cage.setCooking(true);
                        if(!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }else if(heldItem.getItem() instanceof LightningUpgradeItem && !cage.isLightning()){
                        cage.setLightning(true);
                        if(!player.isCreative()) {
                            heldItem.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
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
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
            // Try to add a mob
            if(!cage.hasEntity()){
                // Check if player holds DNA Sampler
                if(heldItem.getItem() instanceof DnaSamplerItem) {
                    DnaSamplerItem sampler = (DnaSamplerItem) heldItem.getItem();
                    // Check if there exists a recipe with given entity type and if the env suits the entity
                    if(MobCageTE.existsEntityFromType(sampler.getEntityType(heldItem)) && cage.isEnvSuitable(player, sampler.getEntityType(heldItem))){
                        cage.setEntityFromType(sampler.getEntityType(heldItem),heldItem);
                        // Clear the sampler's mob type if not in creative
                        if(!player.isCreative()) {
                            sampler.removeEntityType(heldItem);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
                return InteractionResult.PASS;
            }
            // Try to harvest
            if(!cage.isHopping() && cage.isWaitingForHarvest()) {
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.mainInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.envInfo").withStyle(ChatFormatting.GRAY));
        tooltip.add(new TranslatableComponent("block.cagedmobs.mobcage.upgrading").withStyle(ChatFormatting.GRAY));
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

//    // Used for TheOneProbe support
//    @Override
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
//        if(!(world.getTileEntity(data.getPos()) instanceof MobCageTE)) {
//            return;
//        }
//        MobCageTE tile = (MobCageTE) world.getTileEntity(data.getPos());
//        if(tile != null) {
//            if(tile.hasEnvironment() && tile.hasEntity()) {
//                probeInfo.progress((int)(tile.getGrowthPercentage()*100), 100, probeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));
//            }
//            if (tile.hasEnvironment()) {
//                probeInfo.horizontal().text(new TranslationTextComponent("HWYLA.tooltip.cagedmobs.cage.environment"));
//                probeInfo.horizontal().item(tile.getEnvItem()).itemLabel(tile.getEnvItem());
//            }
//            if(tile.hasEntity()){
//                probeInfo.horizontal().text(new StringTextComponent(
//                        new TranslationTextComponent("HWYLA.tooltip.cagedmobs.cage.entity").mergeStyle(TextFormatting.GRAY).getString() +
//                                new TranslationTextComponent(tile.getEntityType().getTranslationKey()).mergeStyle(TextFormatting.GRAY
//                                ).getString()));
//            }
//            // Upgrades
//            if(tile.hasUpgrade()){
//                probeInfo.horizontal().text(new TranslationTextComponent("TOP.tooltip.cagedmobs.cage.upgrades"));
//            }
//            if(tile.isLightning() && tile.isArrow() && tile.isCooking()){
//                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
//            }
//            else if(tile.isLightning() && tile.isCooking()){
//                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance());
//            }
//            else if(tile.isLightning() && tile.isArrow()){
//                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
//            }
//            else if(tile.isCooking() && tile.isArrow()){
//                probeInfo.horizontal().item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance()).item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
//            }
//            else if(tile.isLightning()){
//                probeInfo.horizontal().item(CagedItems.LIGHTNING_UPGRADE.get().getDefaultInstance());
//            }
//            else if(tile.isCooking()){
//               probeInfo.item(CagedItems.COOKING_UPGRADE.get().getDefaultInstance());
//            }
//            else if(tile.isArrow()){
//               probeInfo.horizontal().item(CagedItems.ARROW_UPGRADE.get().getDefaultInstance());
//            }
//
//        }
//    }

}
