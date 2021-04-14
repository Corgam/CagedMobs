package com.corgam.cagedmobs.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.Set;

public class SerializationHelper {

    //String collection

    public static void serializeStringCollection(PacketBuffer buffer, Set<String> categories) {
        buffer.writeInt(categories.size());
        for(String s : categories){
            buffer.writeUtf(s);
        }
    }

    public static void deserializeStringCollection(PacketBuffer buffer, Set<String> categories) {
        final int len = buffer.readInt();
        for(int i=0 ; i < len ; i++){
            categories.add(buffer.readUtf());
        }
    }

    //Block state

    public static void serializeBlockState(PacketBuffer buffer, BlockState renderState) {
        //TODO Non default states
        Block block = renderState.getBlock();
        String locationString = ForgeRegistries.BLOCKS.getKey(block).toString();
        buffer.writeUtf(locationString);
    }

    public static BlockState deserializeBlockState(PacketBuffer buffer) {
        //TODO Non default states
        String locationString = buffer.readUtf();
        ResourceLocation location = new ResourceLocation(locationString);
        final Block block = ForgeRegistries.BLOCKS.getValue(location);
        if(block != null){
            return block.defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    public static BlockState deserializeBlockState(JsonElement json) {
        ResourceLocation location = new ResourceLocation(json.getAsString());
        final Block block = ForgeRegistries.BLOCKS.getValue(location);
        if(block != null){
            return block.defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    // EntityType

    public static EntityType<?> deserializeEntityType(ResourceLocation id, PacketBuffer buffer) {
        final String entityTypeString = buffer.readUtf();
        ResourceLocation res = new ResourceLocation(entityTypeString);
        if(EntityType.byString(res.toString()).isPresent()) {
            return EntityType.byString(res.toString()).get();
        }else{
            return null;
        }
    }

    public static void serializeEntityType(PacketBuffer buffer, EntityType<?> entityType) {
        final String entityTypeString= EntityType.getKey(entityType).toString();
        buffer.writeUtf(entityTypeString);
    }

    public static CompoundNBT serializeEntityTypeNBT(CompoundNBT nbt, EntityType<?> entityType) {
        nbt.putString("entity", EntityType.getKey(entityType).toString());
        return nbt;
    }


    public static EntityType<?> deserializeEntityTypeNBT(CompoundNBT nbt) {
        // Prepare the resourceLocation
        String resString = nbt.getString("entity");
        if(resString.isEmpty()){return null;}
        String[] splitted = resString.split(":");
        ResourceLocation res = new ResourceLocation(splitted[0], splitted[1]);
        // Search for the entity type in the registry and return it
        return ForgeRegistries.ENTITIES.getValue(res);
    }

    public static EntityType<?> deserializeEntityType(ResourceLocation id, JsonObject json) {
        final String entityTypeString= json.getAsJsonPrimitive("entity").getAsString();
        Optional<EntityType<?>> entityType = EntityType.byString(entityTypeString);
        if(entityType.isPresent()) {
            return entityType.get();
        }else {
            throw new IllegalArgumentException("MobDataRecipe with id: " + id + " has an invalid entity key. No entity with given key exists.");
        }
    }
}
