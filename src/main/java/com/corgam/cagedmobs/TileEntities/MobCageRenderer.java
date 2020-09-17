package com.corgam.cagedmobs.TileEntities;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
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

        if(tile.getEnvironment() != null){
            matrix.push();

            matrix.scale(0.74f,0.015f,0.74f);
            matrix.translate(0.17, 5, 0.17);

            //Direction[] dir = new Direction[] { Direction.UP };

            final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

            final IBakedModel model = dispatcher.getModelForState(tile.getEnvironment().getRenderState());
            //final IBakedModel mod = new IBakedModel();
            //final IModelData modelData = model.getModelData(tile.getWorld(), tile.getPos(), tile.getEnvironment().getRenderState(), EmptyModelData.INSTANCE);


            dispatcher.renderBlock(tile.getEnvironment().getRenderState(),matrix,buffer,combinedLightIn,combinedOverlayIn,EmptyModelData.INSTANCE);



            matrix.pop();
        }
        if(tile.hasEntity()){
            matrix.push();
            matrix.translate(0.5D, 0.0D, 0.5D);
            Entity entity = tile.getCachedEntity();
            if (entity != null) {
                float f = 0.53125F;
                float f1 = Math.max(entity.getWidth(), entity.getHeight());
                if ((double)f1 > 1.0D) {
                    f /= f1;
                }

                matrix.translate(0.0D, (double)0.4F, 0.0D);
                matrix.rotate(Vector3f.YP.rotationDegrees((float) MathHelper.lerp((double)partialTicks, 180.D, 180.D) * 10.0F));
                matrix.translate(0.0D, (double)-0.2F, 0.0D);
                matrix.rotate(Vector3f.XP.rotationDegrees(-30.0F));
                matrix.scale(f, f, f);
                Minecraft.getInstance().getRenderManager().renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrix, buffer, combinedLightIn);
            }

            matrix.pop();
        }
    }

    private void renderBlock (BlockState state, World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer, Direction[] sides) {

        final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        final IBakedModel model = dispatcher.getModelForState(state);

        // Find render type
        RenderType type = RenderTypeLookup.func_239221_b_(state);
        for (final RenderType blockType : RenderType.getBlockRenderTypes()) {
            if (RenderTypeLookup.canRenderInLayer(state, blockType)) {
                type = blockType;
            }
        }

        ForgeHooksClient.setRenderLayer(type);

        final IVertexBuilder builder = buffer.getBuffer(type);
        //this.renderModel(dispatcher.getBlockModelRenderer(), world, model, state, pos, matrix, builder, sides);
        dispatcher.renderBlock(state,matrix,buffer,0,0,EmptyModelData.INSTANCE);

        ForgeHooksClient.setRenderLayer(null);
    }

//    public static void renderModel (BlockModelRenderer renderer, IBlockDisplayReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, IVertexBuilder buffer, Direction[] sides) {
//
//        final IModelData modelData = model.getModelData(world, pos, state, EmptyModelData.INSTANCE);
//
//        // Renders only the sided model quads.
//        for (final Direction side : sides) {
//
//            RANDOM.setSeed(0L);
//            final List<BakedQuad> sidedQuads = model.getQuads(state, side, RANDOM, modelData);
//
//            if (!sidedQuads.isEmpty()) {
//
//                final int lightForSide = WorldRenderer.getPackedLightmapCoords(world, state, pos.offset(side));
//                renderer.renderQuadsFlat(world, state, pos, lightForSide, OverlayTexture.NO_OVERLAY, false, matrix, buffer, sidedQuads, BITS);
//            }
//        }
//
//        // Renders the non-sided model quads.
//        RANDOM.setSeed(0L);
//        final List<BakedQuad> unsidedQuads = model.getQuads(state, null, RANDOM, modelData);
//
//        if (!unsidedQuads.isEmpty()) {
//
//            renderer.renderQuadsFlat(world, state, pos, -1, OverlayTexture.NO_OVERLAY, true, matrix, buffer, unsidedQuads, BITS);
//        }
//    }
}
