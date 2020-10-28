package com.corgam.cagedmobs.blocks;

import com.corgam.cagedmobs.tileEntities.MobCageTE;
import com.corgam.cagedmobs.items.CookingUpgradeItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.LightningUpgradeItem;
import com.corgam.cagedmobs.items.UpgradeItem;
import com.corgam.cagedmobs.setup.CagedItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class MobCageBlock extends ContainerBlock {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

    public MobCageBlock(Properties properties) {
        super(properties);
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new MobCageTE(false);
    }

    @Override
    public boolean hasTileEntity (BlockState state) {
        return true;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        // If on client leave the functionality for the server only
        if(worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }
        // Error check for tile entity
        final TileEntity tile = worldIn.getTileEntity(pos);
        if( tile instanceof MobCageTE) {
            final MobCageTE cage = (MobCageTE) tile;
            final ItemStack heldItem = player.getHeldItem(handIn);
            // Try to remove upgrade, env or mob
            if(player.isSneaking()) {
                if(cage.isCooking() || cage.isLightning()){
                    if(cage.isCooking()){
                        cage.setCooking(false);
                        cage.dropItem(new ItemStack(CagedItems.COOKING_UPGRADE.get(),1));
                        return ActionResultType.SUCCESS;
                    }else if(cage.isLightning()){
                        cage.setLightning(false);
                        cage.dropItem(new ItemStack(CagedItems.LIGHTNING_UPGRADE.get(),1));
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
            //Try to add an upgrade
            if(!cage.isCooking() || !cage.isLightning()){
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
                }
                return ActionResultType.PASS;
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
                        return ActionResultType.SUCCESS;
                    }
                    return ActionResultType.PASS;
                }
                return ActionResultType.PASS;
            }
            // Try to harvest
            if(!cage.isHopping() && cage.isWaitingForHarvest()) {
                cage.onPlayerHarvest();
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        }
        return ActionResultType.FAIL;
    }
    // Debug only
    public String toStringWithTile(World worldIn, BlockPos pos){
        final TileEntity tile = worldIn.getTileEntity(pos);
        if( tile instanceof MobCageTE) {
            final MobCageTE cage = (MobCageTE) tile;
            String stringEntity = "none";
            if(cage.getEntity() != null) {
                stringEntity = cage.getEntity().getEntityType().toString();
            }
            String hopping = "";
            if(cage.isHopping()) {
                hopping = "Hopping ";
            }
            return hopping + "MobCage (" + stringEntity + ") [" + cage.getCurrentGrowTicks() + "/" + cage.getTotalGrowTicks() + "]";
        }
        return "";
    }

    @Override
    public void onReplaced (BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(state.hasTileEntity() && state.getBlock() != newState.getBlock()){
            final TileEntity tileEntity = worldIn.getTileEntity(pos);
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
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.mainInfo").func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.envInfo").func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("block.cagedmobs.mobcage.upgrading").func_240699_a_(TextFormatting.GRAY));
    }

    /// SHAPE ///

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
