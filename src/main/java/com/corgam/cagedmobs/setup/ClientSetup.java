package com.corgam.cagedmobs.setup;

import com.corgam.cagedmobs.blockEntities.MobCageRenderer;
import com.corgam.cagedmobs.registers.CagedBlockEntity;
import com.corgam.cagedmobs.registers.CagedBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    // Set the render type of blocks
    public static void renderLayerSetup(FMLClientSetupEvent event) {

        // Set the render layers for blocks
        ItemBlockRenderTypes.setRenderLayer(CagedBlocks.MOB_CAGE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CagedBlocks.HOPPING_MOB_CAGE.get(), RenderType.cutout());

        // Register the block entities renderers
        BlockEntityRenderers.register(CagedBlockEntity.MOB_CAGE.get(), MobCageRenderer::new);
    }
}
