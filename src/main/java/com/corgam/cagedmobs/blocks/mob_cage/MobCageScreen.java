package com.corgam.cagedmobs.blocks.mob_cage;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.helpers.EntityRendererHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public class MobCageScreen extends ContainerScreen<MobCageContainer> {

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
    public MobCageScreen(MobCageContainer pMenu, PlayerInventory pPlayerInventory, ITextComponent pTitle) {
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
    public void render(MatrixStack pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        // Render entity
        TileEntity blockEntity = this.menu.player.level.getBlockEntity(this.menu.pos);
        if(blockEntity instanceof MobCageBlockEntity && ((MobCageBlockEntity) blockEntity).getEntity().isPresent()){
            MobCageBlockEntity cageBE = (MobCageBlockEntity) blockEntity;
            Optional<Entity> entity = EntityRendererHelper.createEntity(this.menu.player.level, cageBE.getEntity().get().getEntityType());
            if(entity.isPresent()){
                rotation = (rotation+ 0.5f)% 360;
                //enableScissor(this.leftPos+62, this.topPos+17, this.leftPos+114, this.topPos+87);
                EntityRendererHelper.renderEntity(pGuiGraphics, this.leftPos + 87, this.topPos + 125, yaw, 70, rotation, entity.get() );
                //disableScissor();
                // Update yaw
                yaw = (yaw + 1.5) % 720.0F;
            }
        }
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(MatrixStack pGuiGraphics, float pPartialTicks, int pX, int pY) {
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(this.GUI);
        blit(pGuiGraphics, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
        // Render item outline for environment slot
        Slot envSlot = this.menu.getEnvironmentSlot();
        if(!envSlot.hasItem()){
//            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(this.ENVIRONMENT_SLOT_OUTLINE);
            blit(pGuiGraphics, leftPos+envSlot.x, topPos+envSlot.y, 0, 0, 16, 16, 16, 16);
        }
        // Render item outlines for upgrade slots
        int i = 1;
        for(Slot slot : this.menu.getUpgradeSlots()){
            if (!slot.hasItem()) {
//                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.minecraft.getTextureManager().bind(this.UPGRADE_SLOT_OUTLINE);
                blit(pGuiGraphics, leftPos+slot.x, topPos+slot.y, this.imageWidth + (16* (i-1)), 0, 16, 16, 16, 16);
                i++;
            }
        }
    }
}
