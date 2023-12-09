package com.corgam.cagedmobs.blocks.mob_cage;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.helpers.EntityRendererHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class MobCageScreen extends AbstractContainerScreen<MobCageContainer> {

    // The path to the GUI image
    private final ResourceLocation GUI = new ResourceLocation(CagedMobs.MOD_ID, "textures/gui/mob_cage.png");
    private final ResourceLocation UPGRADE_SLOT_OUTLINE = new ResourceLocation(CagedMobs.MOD_ID, "textures/gui/upgrade_slot.png");
    private final ResourceLocation ENVIRONMENT_SLOT_OUTLINE = new ResourceLocation(CagedMobs.MOD_ID, "textures/gui/environment_slot.png");

    private static float rotation = 0.0f;
    private static double yaw = 0;

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
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        // Render entity
        BlockEntity blockEntity = this.menu.player.level().getBlockEntity(this.menu.pos);
        if(blockEntity instanceof MobCageBlockEntity cageBE && cageBE.getEntity().isPresent()){
            Optional<Entity> entity;
            if(cageBE.getColor() != -1){
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("Color",cageBE.getColor());
                entity = EntityRendererHelper.createEntity(this.menu.player.level(), cageBE.getEntity().get().getEntityType(), nbt);
            }else{
                entity = EntityRendererHelper.createEntity(this.menu.player.level(), cageBE.getEntity().get().getEntityType(), null);
            }
            if(entity.isPresent()){
                rotation = (rotation+ 0.5f)% 360;
                pGuiGraphics.enableScissor(this.leftPos+62, this.topPos+17, this.leftPos+114, this.topPos+87);
                EntityRendererHelper.renderEntity(pGuiGraphics, this.leftPos + 87, this.topPos + 125, yaw, 70, rotation, entity.get() );
                pGuiGraphics.disableScissor();
                // Update yaw
                yaw = (yaw + 1.5) % 720.0F;
            }
        }
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
