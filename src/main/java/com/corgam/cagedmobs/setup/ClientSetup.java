package com.corgam.cagedmobs.setup;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

public class ClientSetup {
    // Set the render type of blocks
    public static void renderLayerSetup(FMLClientSetupEvent event) {

        //RenderTypeLookup.setRenderLayer(CagedBlocks.MOB_CAGE.get(), RenderType.cutout());
        //RenderTypeLookup.setRenderLayer(CagedBlocks.HOPPING_MOB_CAGE.get(), RenderType.cutout());

        // Bind Tile Entity Renderers
        //ClientRegistry.bindTileEntityRenderer(CagedTE.MOB_CAGE.get(), MobCageRenderer::new);
    }
}
