package com.corgam.cagedmobs.block_entities;

import com.corgam.cagedmobs.CagedMobs;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

@OnlyIn(Dist.CLIENT)
public class MobCageRenderer implements BlockEntityRenderer<OldTestEntity> {

    public MobCageRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(OldTestEntity tile, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        if(tile.getEnvironment() != null && CagedMobs.CLIENT_CONFIG.shouldEnvsRender()){
            matrix.pushPose();
            matrix.scale(0.74f,0.015f,0.74f);
            matrix.translate(0.17, 5, 0.17);

            final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            dispatcher.renderSingleBlock(tile.getEnvironment().getRenderState(), matrix, buffer, combinedLightIn, combinedOverlayIn);
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
                    matrix.scale(growthPercentage,growthPercentage,growthPercentage);
                }else{
                    matrix.scale(maxSize, maxSize, maxSize);
                }
                if(entity instanceof Sheep){
                    ((Sheep) entity).setColor(DyeColor.byId(tile.getColor()));
                }
                // Decide what to do on which side
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    Minecraft.getInstance().getEntityRenderDispatcher().render(entity,0.0D , 0.0D, getEntityZ(entity), 0.0F, partialTicks, matrix, buffer, combinedLightIn);
                });
            }

            matrix.popPose();
        }
    }

    private double getEntityZ(Entity entity){
        if(entity instanceof Dolphin){
            return 0.25D;
        }
        if(entity instanceof ElderGuardian){
            return 1.2D;
        }
        if( entity instanceof Guardian){
            return 0.7D;
        }
        if( entity instanceof Hoglin || entity instanceof Zoglin){
            return -0.3F;
        }
        return 0.0D;
    }

    private float getEntitySize(Entity entity){
        if(entity instanceof Dolphin ||
                entity instanceof Panda ||
                entity instanceof Pig ||
                entity instanceof PolarBear ||
                entity instanceof Squid ||
                entity instanceof Turtle ||
                entity instanceof Hoglin ||
                entity instanceof Ravager ||
                entity instanceof Shulker ||
                entity instanceof Zoglin
        ){
            return 0.32F;
        }
        if(entity instanceof AbstractHorse){
            return 0.4F;
        }
        if(entity instanceof Guardian || entity instanceof Phantom){
            return 0.25F;
        }
        if(entity instanceof EnderDragon){
            return 0.8F;
        }
        return 0.5F;
    }

}
