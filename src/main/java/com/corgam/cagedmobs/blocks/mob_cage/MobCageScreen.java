package com.corgam.cagedmobs.blocks.mob_cage;

import com.corgam.cagedmobs.setup.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class MobCageScreen extends AbstractContainerScreen<MobCageContainer> {

    // The path to the GUI image
    private final ResourceLocation GUI = new ResourceLocation(Constants.MOD_ID, "textures/gui/mob_cage.png");
    private final ResourceLocation UPGRADE_SLOT_OUTLINE = new ResourceLocation(Constants.MOD_ID, "textures/gui/upgrade_slot.png");
    private final ResourceLocation ENVIRONMENT_SLOT_OUTLINE = new ResourceLocation(Constants.MOD_ID, "textures/gui/environment_slot.png");


    /**
     * Creates the cage screen rendered on the client side.
     * @param pMenu container
     * @param pPlayerInventory player inventory
     * @param pTitle screen name
     */
    public MobCageScreen(MobCageContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 183;
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
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(GUI, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
        // Render item outline for environment slot
        Slot envSlot = this.menu.getEnvironmentSlot();
        if(!envSlot.hasItem()){
            pGuiGraphics.blit(ENVIRONMENT_SLOT_OUTLINE, leftPos+envSlot.x, topPos+envSlot.y, 0, 0, 16, 16, 16, 16);
        }
        // Render item outlines for upgrade slots
        int i = 1;
        for(Slot slot : this.menu.getUpgradeSlots()){
            if (!slot.hasItem()) {
                pGuiGraphics.blit(UPGRADE_SLOT_OUTLINE, leftPos+slot.x, topPos+slot.y, this.imageWidth + (16* (i-1)), 0, 16, 16, 16, 16);
                i++;
            }
        }
    }
}
