package com.corgam.cagedmobs.tileEntities;

import com.corgam.cagedmobs.CagedMobs;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class MobCageRenderer extends TileEntityRenderer<MobCageTE> {

    public MobCageRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(MobCageTE tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

        if(tile.getEnvironment() != null && CagedMobs.CLIENT_CONFIG.shouldEnvsRender()){
            matrix.push();
            matrix.scale(0.74f,0.015f,0.74f);
            matrix.translate(0.17, 5, 0.17);

            final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

            //final IBakedModel model = dispatcher.getModelForState(tile.getEnvironment().getRenderState());

            dispatcher.renderBlock(tile.getEnvironment().getRenderState(),matrix,buffer,combinedLightIn,combinedOverlayIn,EmptyModelData.INSTANCE);



            matrix.pop();
        }
        if(tile.hasEntity() && CagedMobs.CLIENT_CONFIG.shouldEntitiesRender()){
            matrix.push();
            matrix.translate(0.5D, 0.0D, 0.5D);
            Entity entity = tile.getCachedEntity();
            if (entity != null) {
                float maxSize = 0.42F;
                float maxEntityDimension = Math.max(entity.getWidth(), entity.getHeight());
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
                Minecraft.getInstance().getRenderManager().renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrix, buffer, combinedLightIn);
            }

            matrix.pop();
        }
    }
}
