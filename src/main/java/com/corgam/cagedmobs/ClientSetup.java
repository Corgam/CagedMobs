package com.corgam.cagedmobs;

import com.corgam.cagedmobs.blocks.mob_cage.MobCageRenderer;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageScreen;
import com.corgam.cagedmobs.registers.CagedBlockEntities;
import com.corgam.cagedmobs.registers.CagedBlocks;
import com.corgam.cagedmobs.registers.CagedContainers;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    // Set the render type of blocks
    public static void renderLayerSetup(FMLClientSetupEvent event) {
        // Set the render layers for blocks
        RenderTypeLookup.setRenderLayer(CagedBlocks.MOB_CAGE.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(CagedBlocks.HOPPING_MOB_CAGE.get(), RenderType.cutout());
        // Register the block entities renderers
        ClientRegistry.bindTileEntityRenderer(CagedBlockEntities.MOB_CAGE_BLOCK_ENTITY.get(), MobCageRenderer::new);
        event.enqueueWork(() -> {
            // Connect the cage container and the screen
            ScreenManager.register(CagedContainers.CAGE_CONTAINER.get(), MobCageScreen::new);
        });
    }
}
