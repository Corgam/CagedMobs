package com.corgam.cagedmobs.tileEntities;

import com.corgam.cagedmobs.CagedMobs;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;

@OnlyIn(Dist.CLIENT)
public class MobCageRenderer extends TileEntityRenderer<MobCageTE> {

    public MobCageRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MobCageTE tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(tile.getEnvironment() != null && CagedMobs.CLIENT_CONFIG.shouldEnvsRender()){
            matrix.pushPose();
            matrix.scale(0.74f,0.015f,0.74f);
            matrix.translate(0.17, 5, 0.17);

            final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
            dispatcher.renderBlock(tile.getEnvironment().getRenderState(), matrix, buffer, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);

            matrix.popPose();
        }
        if(tile.hasEntity() && CagedMobs.CLIENT_CONFIG.shouldEntitiesRender()){
            matrix.pushPose();
            matrix.translate(0.5D, 0.0D, 0.5D);
            Entity entity = tile.getCachedEntity(tile.getLevel());
            if (entity != null) {
                float maxSize = getEntitySize(entity);
                float maxEntityDimension = Math.max(entity.getBbWidth(), entity.getBbHeight());
                // If entity is bigger then 1.0D, scale it down.
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
                if(entity instanceof SheepEntity){
                    ((SheepEntity) entity).setColor(DyeColor.byId(tile.getColor()));
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