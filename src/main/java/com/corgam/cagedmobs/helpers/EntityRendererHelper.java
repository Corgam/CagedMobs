package com.corgam.cagedmobs.helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;

import java.util.Optional;

import static com.corgam.cagedmobs.CagedMobs.LOGGER;

public class EntityRendererHelper {


    /**
     * Tries to create an entity inside a given level.
     * @param level level to create entity in
     * @return optional entity
     */
    public static Optional<Entity> createEntity(Level level, EntityType<?> entityType){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", EntityType.getKey(entityType).toString());
        // Create the entity
        Optional<Entity> entity = Optional.empty();
        if(level != null && level.isClientSide()){
            entity = EntityType.create(nbt, level);
        }
        return entity;
    }

    /**
     * Renders entity inside GUI.
     */
    public static void renderEntity(GuiGraphics graphics, int x, int y, double yaw, double pitch, float rotation, Entity entity) {
        // Push pose
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        // Get offset and scale
        float scale = EntityRendererHelper.getScaleForEntityType(entity);
        y -= EntityRendererHelper.getOffsetForEntityType(entity);
        rotation -= EntityRendererHelper.getOffsetForEntityType(entity);
        // Translate the entity to right position
        matrixStack.translate(x, y, 50F);
        // Scale the entity
        matrixStack.scale(-scale, scale, scale);
        // Rotate the entity so it's not upside down
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
        // Rotate the entity around
        matrixStack.mulPose(Axis.YP.rotationDegrees(rotation));
        // Render the entity
        MultiBufferSource.BufferSource buff = Minecraft.getInstance().renderBuffers().bufferSource();
        try{
            Minecraft.getInstance().getEntityRenderDispatcher().render(entity,0.0D,0.0D,0.0D,0.0F,0.0F,matrixStack,buff,15728880);
        }catch (Exception e){
            matrixStack.translate(x, y, -50F);
            matrixStack.scale(scale, -scale, -scale);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
            LOGGER.error("[CagedMobs] Error with rendering entity in JEI!", e);
        }
        buff.endBatch();
        matrixStack.popPose();
    }

    /**
     * Returns a scale for a given entity.
     * @param entity entity to check
     * @return scale
     */
    public static float getScaleForEntityType(Entity entity){
        float width = entity.getBbWidth();
        float height = entity.getBbHeight();
        // Some hardcoded values
        if(entity.getType().toString().contains("twilightforest.ur_ghast")){return 2.0F;}
        if(entity.getType().toString().contains("greekfantasy.charybdis")){return 3.0F;}
        if(entity.getType().toString().contains("twilightforest.hydra")){return 3.2F;}
        if(entity.getType().toString().contains("twilightforest.yeti_alpha")){return 6.0F;}
        if(entity.getType().toString().contains("minecraft.ender_dragon")) {return 5.0F;}
        if(entity.getType().toString().contains("twilightforest.armored_giant")) {return 6.0F;}
        if(entity.getType().toString().contains("twilightforest.giant_miner")) {return 6.0F;}
        if(entity.getType().toString().contains("twilightforest.mini_ghast")) {return 10.0F;}
        if(entity.getType().toString().contains("outvoted:kraken")) {return 2.0F;}
        if(entity.getType().toString().contains("mowziesmobs:frostmaw")) {return 10.0F;}
        if(entity.getType().toString().contains("alexsmobs:cachalot_whale")) {return 0.2F;}
        if(entity.getType().toString().contains("greekfantasy:giant_boar")) {return 10.0F;}
        if(entity.getType().toString().contains("alexsmobs:crocodile")) {return 6.0F;}
        if(entity.getType().toString().contains("alexsmobs:hammerhead_shark")) {return 6.0F;}
        if(entity.getType().toString().contains("upgrade_aquatic:great_thrasher")) {return 2.0F;}
        if(entity.getType().toString().contains("fireandice:cyclops")) {return 2.0F;}
        if(entity.getType().toString().contains("alexsmobs:cachalot_whale")) {return 0.5F;}
        if(entity.getType().toString().contains("alexsmobs:laviathan")) {return 0.5F;}
        if(entity.getType().toString().contains("alexsmobs:void_worm")) {return 1.0F;}
        if(entity instanceof ElderGuardian) {return 10.0F;}
        if(entity instanceof AbstractFish) {return 25.0F;}
        if(entity instanceof Ghast) {return 5.2F;}
        if(entity instanceof Sniffer) {return 16F;}
        // Handling some of the other cases
        if(width <= height){
            if(height >= 3){
                return 10.0f;
            }else if(height >= 2.5){
                return 15.0f;
            }
            else if(height >= 1.9){
                return 18.0F;
            }
        }
        return 20.0F;
    }

    /**
     * Returns the Y render offset for a given entity.
     * @param entity the entity to check
     * @return y offset
     */
    public static int getOffsetForEntityType(Entity entity){
        if(entity instanceof Phantom ||
                entity instanceof AbstractFish ||
                entity instanceof EnderDragon ||
                entity instanceof Dolphin ||
                entity instanceof Guardian ||
                entity instanceof Turtle
        ){
            return 60;
        }else if(entity instanceof Ghast || entity instanceof Squid){
            return 65;
        }
        return 50;
    }
}
