package com.corgam.cagedmobs.helpers;

import com.corgam.cagedmobs.blocks.mob_cage.MobCageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Helper class for emitting cage upgrades' particles on client side.
 */

@OnlyIn(Dist.CLIENT)
public class UpgradeItemsParticles {

    /**
     * Emits cooking upgrade particles from the cage.
     * @param blockEntity the block entity to emit particles from
     */
    public static void emitCookingParticles(MobCageBlockEntity blockEntity){
        Level level = blockEntity.getLevel();
        if (level != null && level.isClientSide()) {
            if (level.random.nextInt(10) == 0) {
                BlockPos blockpos = blockEntity.getBlockPos();
                double d3 = (double) blockpos.getX() + level.random.nextDouble();
                double d4 = (double) blockpos.getY() + (level.random.nextDouble()/3);
                double d5 = (double) blockpos.getZ() + level.random.nextDouble();
                if(!blockEntity.getBlockState().getValue(BlockStateProperties.WATERLOGGED)){
                    // If not waterlogged emit fire particles
                    level.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                    level.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                }else{
                    // If waterlogged emit blue fire particles
                    level.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                }

            }
        }
    }

    /**
     * Emits lightning upgrade particles from the cage.
     * @param blockEntity the block entity to emit particles from
     */
    public static void emitLightningParticles(MobCageBlockEntity blockEntity){
        Level level = blockEntity.getLevel();
        if (level != null && level.isClientSide()) {
            if (level.random.nextInt(15) == 0) {
                BlockPos blockpos = blockEntity.getBlockPos();
                double d3 = (double) blockpos.getX() + level.random.nextDouble();
                double d4 = (double) blockpos.getY() + level.random.nextDouble();
                double d5 = (double) blockpos.getZ() + level.random.nextDouble();
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * Emits arrow upgrade particles from the cage.
     * @param blockEntity the block entity to emit particles from
     */
    public static void emitArrowParticles(MobCageBlockEntity blockEntity){
        Level level = blockEntity.getLevel();
        if (level != null && level.isClientSide()) {
            if (level.random.nextInt(30) == 0) {
                BlockPos blockpos = blockEntity.getBlockPos();
                double d3 = (double) blockpos.getX() + 0.4 + (level.random.nextDouble()/5);
                double d4 = (double) blockpos.getY() + 0.8;
                double d5 = (double) blockpos.getZ() +  0.4 + (level.random.nextDouble()/5);
                level.addParticle(ParticleTypes.CRIT, d3, d4, d5, 0.0D, -0.5D, 0.0D);
            }
        }
    }

    /**
     * Emits experience upgrade particles from the cage.
     * @param blockEntity the block entity to emit particles from
     */
    public static void emitExperienceParticles(MobCageBlockEntity blockEntity){
        Level level = blockEntity.getLevel();
        if (level != null && level.isClientSide()) {
            if (level.random.nextInt(20) == 0) {
                BlockPos blockpos = blockEntity.getBlockPos();
                double d3 = (double) blockpos.getX() + level.random.nextDouble();
                double d4 = (double) blockpos.getY() + 0.9;
                double d5 = (double) blockpos.getZ() + level.random.nextDouble();
                level.addParticle(ParticleTypes.TOTEM_OF_UNDYING, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * Emits fortune upgrade particles from the cage.
     * @param blockEntity the block entity to emit particles from
     */
    public static void emitFortuneParticles(MobCageBlockEntity blockEntity){
        Level level = blockEntity.getLevel();
        if (level != null && level.isClientSide()) {
            if (level.random.nextInt(20) == 0) {
                BlockPos blockpos = blockEntity.getBlockPos();
                double d3 = (double) blockpos.getX() + level.random.nextDouble();
                double d4 = (double) blockpos.getY() + (level.random.nextDouble()/2);
                double d5 = (double) blockpos.getZ() + level.random.nextDouble();
                level.addParticle(ParticleTypes.HAPPY_VILLAGER, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /**
     * Emits speed upgrade particles from the cage.
     * Used for all levels of speed upgrade.
     * @param blockEntity the block entity to emit particles from
     */
    public static void emitSpeedParticles(MobCageBlockEntity blockEntity){
        Level level = blockEntity.getLevel();
        if (level != null && level.isClientSide()) {
            if (level.random.nextInt(25) == 0) {
                BlockPos blockpos = blockEntity.getBlockPos();
                double d3 = (double) blockpos.getX() + level.random.nextDouble();
                double d4 = (double) blockpos.getY() + (level.random.nextDouble()/2);
                double d5 = (double) blockpos.getZ() + level.random.nextDouble();
                level.addParticle(ParticleTypes.EFFECT, d3, d4, d5, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
