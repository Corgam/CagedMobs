package com.corgam.cagedmobs.setup;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {
    public static void renderLayerSetup() {
        RenderTypeLookup.setRenderLayer(CagedBlocks.MOB_CAGE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(CagedBlocks.HOPPING_MOB_CAGE.get(), RenderType.getCutout());
    }
}
