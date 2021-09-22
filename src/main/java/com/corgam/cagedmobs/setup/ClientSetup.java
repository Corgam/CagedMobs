package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.blockEntities.MobCageRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

public class ClientSetup {
    // Set the render type of blocks
    public static void renderLayerSetup(FMLClientSetupEvent event) {

        ItemBlockRenderTypes.setRenderLayer(CagedBlocks.MOB_CAGE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CagedBlocks.HOPPING_MOB_CAGE.get(), RenderType.cutout());

        // Bind Tile Entity Renderers
        //ClientRegistry.bindTileEntityRenderer(CagedBlockEntity.MOB_CAGE.get(), MobCageRenderer::new);
    }
}
