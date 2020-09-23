package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.tileEntities.MobCageRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    // Set the render type of blocks
    public static void renderLayerSetup(FMLClientSetupEvent event) {

        RenderTypeLookup.setRenderLayer(CagedBlocks.MOB_CAGE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(CagedBlocks.HOPPING_MOB_CAGE.get(), RenderType.getCutout());

        // Bind Tile Entity Renderers
        ClientRegistry.bindTileEntityRenderer(CagedTE.MOB_CAGE.get(), MobCageRenderer::new);
    }
}
