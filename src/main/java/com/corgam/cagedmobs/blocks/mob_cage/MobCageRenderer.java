package com.corgam.cagedmobs.blocks.mob_cage;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

@OnlyIn(Dist.CLIENT)
public class MobCageRenderer extends TileEntityRenderer<MobCageBlockEntity> {

    public MobCageRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MobCageBlockEntity tile, float pPartialTicks, MatrixStack matrix, IRenderTypeBuffer pBuffer, int pCombinedLight, int pCombinedOverlay){
        if(tile.getEnvironmentData() != null && CagedMobs.CLIENT_CONFIG.shouldEnvsRender()){
            matrix.pushPose();
            matrix.scale(0.74f,0.015f,0.74f);
            matrix.translate(0.17, 5, 0.17);

            final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            dispatcher.renderSingleBlock(tile.getEnvironmentData().getRenderState(), matrix, pBuffer, pCombinedLight, pCombinedOverlay);
            matrix.popPose();
        }
        if(tile.hasEntity() && CagedMobs.CLIENT_CONFIG.shouldEntitiesRender()){
            matrix.pushPose();
            matrix.translate(0.5D, 0.0D, 0.5D);
            Entity entity = tile.getCachedEntity(tile.getLevel());
            if (entity != null) {
                float maxSize = getEntitySize(entity);
                float maxEntityDimension = Math.max(entity.getBbWidth(), entity.getBbHeight());
                // If entity is bigger than 1.0D, scale it down.
                if ((double)maxEntityDimension > 1.0D) {
                    maxSize /= maxEntityDimension;
                }
                matrix.translate(0.0D, (double)0.1F, 0.0D);
                if(CagedMobs.CLIENT_CONFIG.shouldGrowthRender()){
                    float growthPercentage = tile.getGrowthPercentage() * maxSize;
                    // Replace growth percentage if creative upgrade is inside to prevent rendering flickering
                    if(tile.hasUpgrades(CagedItems.CREATIVE_UPGRADE.get(),1 )){
                        growthPercentage = maxSize;
                    }
                    matrix.scale(growthPercentage,growthPercentage,growthPercentage);
                }else{
                    matrix.scale(maxSize, maxSize, maxSize);
                }
                if(entity instanceof SheepEntity){
                    ((SheepEntity) entity).setColor(DyeColor.byId(tile.getColor()));
                }
                // Decide what to do on which side
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    Minecraft.getInstance().getEntityRenderDispatcher().render(entity,0.0D , 0.0D, getEntityZ(entity), 0.0F, pPartialTicks, matrix, pBuffer, pCombinedLight);
                });
            }

            matrix.popPose();
        }
    }

    private double getEntityZ(Entity entity){
        if(entity instanceof DolphinEntity){
            return 0.25D;
        }
        if(entity instanceof ElderGuardianEntity){
            return 1.2D;
        }
        if( entity instanceof GuardianEntity){
            return 0.7D;
        }
        if( entity instanceof HoglinEntity || entity instanceof ZoglinEntity){
            return -0.3F;
        }
        return 0.0D;
    }

    private float getEntitySize(Entity entity){
        if(entity instanceof DolphinEntity ||
                entity instanceof PandaEntity ||
                entity instanceof PigEntity ||
                entity instanceof PolarBearEntity ||
                entity instanceof SquidEntity ||
                entity instanceof TurtleEntity ||
                entity instanceof HoglinEntity ||
                entity instanceof RavagerEntity ||
                entity instanceof ShulkerEntity ||
                entity instanceof ZoglinEntity
        ){
            return 0.32F;
        }
        if(entity instanceof AbstractHorseEntity){
            return 0.4F;
        }
        if(entity instanceof GuardianEntity || entity instanceof PhantomEntity){
            return 0.25F;
        }
        if(entity instanceof EnderDragonEntity){
            return 0.8F;
        }
        return 0.5F;
    }

}
