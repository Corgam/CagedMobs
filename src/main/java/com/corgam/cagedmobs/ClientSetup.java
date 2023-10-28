package com.corgam.cagedmobs;

import com.corgam.cagedmobs.blocks.mob_cage.MobCageRenderer;
import com.corgam.cagedmobs.blocks.mob_cage.MobCageScreen;
import com.corgam.cagedmobs.registers.CagedBlockEntities;
import com.corgam.cagedmobs.registers.CagedBlocks;
import com.corgam.cagedmobs.registers.CagedContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {
    // Set the render type of blocks
    public static void renderLayerSetup(FMLClientSetupEvent event) {
        // Register the block entities renderers
        BlockEntityRenderers.register(CagedBlockEntities.MOB_CAGE_BLOCK_ENTITY.get(), MobCageRenderer::new);
        event.enqueueWork(() -> {
            // Connect the cage container and the screen
            MenuScreens.register(CagedContainers.CAGE_CONTAINER.get(), MobCageScreen::new);
        });
    }
}
