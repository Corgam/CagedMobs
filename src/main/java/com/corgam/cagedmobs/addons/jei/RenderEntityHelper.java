package com.corgam.cagedmobs.addons.jei;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.function.Function;

public class RenderEntityHelper {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static float rotation = 0.0f;
    private static double yaw = 0;

    public static void drawInfo(MobData recipe, int recipeWidth, int recipeHeight, PoseStack matrixStack, double mouseX, double mouseY) {
        // Proper entity
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", Registry.ENTITY_TYPE.getKey(recipe.getEntityType()).toString());
        SpawnData renderedEntity = new SpawnData(nbt, Optional.empty());
        if(Minecraft.getInstance().getSingleplayerServer() != null){ // When at single player or single player server
            LivingEntity livingEntity = (LivingEntity) EntityType.loadEntityRecursive(renderedEntity.getEntityToSpawn(), Minecraft.getInstance().getSingleplayerServer().overworld(), Function.identity());
            if (livingEntity != null) {
                float scale = getScaleForEntityType(livingEntity);
                int offsetY = getOffsetForEntityType(livingEntity);
                // Add rotation
                rotation = (rotation+ 0.5f)% 360;
                // Render the entity
                renderEntity(
                        matrixStack,
                        33, 120 - offsetY, scale,
                        38 - yaw,
                        70 - offsetY,
                        livingEntity
                );
                // Update yaw
                yaw = (yaw + 1.5) % 720.0F;
            }
        }else if(Minecraft.getInstance().getCurrentServer() != null){ // When at dedicated server
            Level level = Minecraft.getInstance().level;
            if(level != null){
                LivingEntity livingEntity = (LivingEntity) EntityType.loadEntityRecursive(renderedEntity.getEntityToSpawn(), level, Function.identity());
                if (livingEntity != null) {
                    float scale = getScaleForEntityType(livingEntity);
                    int offsetY = getOffsetForEntityType(livingEntity);
                    // Add rotation
                    rotation = (rotation+ 0.5f)% 360;
                    // Render the entity
                    renderEntity(
                            matrixStack,
                            33, 120 - offsetY, scale,
                            38 - yaw,
                            70 - offsetY,
                            livingEntity
                    );
                    // Update yaw
                    yaw = (yaw + 1.5) % 720.0F;
                }
            }
        }
    }

    public static void renderEntity(PoseStack matrixStack, int x, int y, float scale, double yaw, double pitch, LivingEntity entity) {
        matrixStack.pushPose();
        // Translate the entity to right position
        matrixStack.translate(x, y, 50F);
        // Scale the entity
        matrixStack.scale(-scale, scale, scale);
        // Rotate the entity so it's not upside down
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
        // Rotate the entity around
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));

        // Render the entity
        MultiBufferSource.BufferSource buff = Minecraft.getInstance().renderBuffers().bufferSource();
        try{
            Minecraft.getInstance().getEntityRenderDispatcher().render(entity,0.0D,0.0D,0.0D,0.0F,0.0F,matrixStack,buff,15728880);
        }catch (Exception e){
            matrixStack.translate(x, y, -50F);
            matrixStack.scale(scale, -scale, -scale);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));

            CagedMobs.LOGGER.error("Error with rendering entity in JEI!(CagedMobs)", e);
        }
        buff.endBatch();
        matrixStack.popPose();
    }

    private static float getScaleForEntityType(LivingEntity entity){
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
        if(entity instanceof  AbstractFish) {return 25.0F;}
        if(entity instanceof Ghast) {return 5.2F;}

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

    private static int getOffsetForEntityType(LivingEntity entity){
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

    public static int getTicks(MobData entity){
        return entity.getTotalGrowTicks();
    }

    public static int getSeconds(MobData entity){
        return getTicks(entity)/20;
    }
}
