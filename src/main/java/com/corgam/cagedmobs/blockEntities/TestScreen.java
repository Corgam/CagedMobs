package com.corgam.cagedmobs.blockEntities;

import com.corgam.cagedmobs.setup.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class TestScreen extends AbstractContainerScreen<TestEntityContainer> {

    // The path to the GUI image
    private final ResourceLocation GUI = new ResourceLocation(Constants.MOD_ID, "textures/gui/mob_cage.png");
    private final ResourceLocation UPGRADE_SLOT_OUTLINE = new ResourceLocation(Constants.MOD_ID, "textures/gui/upgrade_slot.png");

    /**
     * Creates the cage screen rendered on the client side.
     * @param pMenu container
     * @param pPlayerInventory player inventory
     * @param pTitle screen name
     */
    public TestScreen(TestEntityContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 179;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    /**
     * Makes the background darker and renders the item tooltips.
     * @param pGuiGraphics graphics stack
     * @param pMouseX x coord of the mouse
     * @param pMouseY y coord of the mouse
     * @param pPartialTick partial tick
     */
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    /**
     * Renders the main GUI of the cage.
     * @param pGuiGraphics graphics stack
     * @param pPartialTick partial tick
     * @param pMouseX x coord of the mouse
     * @param pMouseY y coord of the mouse
     */
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(GUI, x, y, 0, 0, this.imageWidth, this.imageHeight);
        // Render item outlines for slots
        int i = 1;
        for(Slot slot : this.menu.getUpgradeSlots()){
            if (!slot.hasItem()) {
                pGuiGraphics.blit(UPGRADE_SLOT_OUTLINE, 134, 19*i, this.imageWidth + 16, 0, 16, 16);
                i++;
            }
        }
    }
}
