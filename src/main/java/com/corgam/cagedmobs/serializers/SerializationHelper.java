package com.corgam.cagedmobs.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SerializationHelper {

    //String collection
    public static void serializeStringCollection(FriendlyByteBuf buffer, List<String> categories) {
        buffer.writeInt(categories.size());
        for(String s : categories){
            buffer.writeUtf(s);
        }
    }

    public static void deserializeStringCollection(FriendlyByteBuf buffer, List<String> categories) {
        final int len = buffer.readInt();
        for(int i=0 ; i < len ; i++){
            categories.add(buffer.readUtf());
        }
    }

    // Block

    public static void serializeBlock(FriendlyByteBuf buffer, Block renderBlock) {
        String locationString = ForgeRegistries.BLOCKS.getKey(renderBlock).toString();
        buffer.writeUtf(locationString);
    }

    public static Block deserializeBlock(FriendlyByteBuf buffer) {
        String locationString = buffer.readUtf();
        ResourceLocation location = new ResourceLocation(locationString);
        return ForgeRegistries.BLOCKS.getValue(location);
    }

    //Block state

    public static void serializeBlockState(FriendlyByteBuf buffer, BlockState renderState) {
        Block block = renderState.getBlock();
        String locationString = ForgeRegistries.BLOCKS.getKey(block).toString();
        buffer.writeUtf(locationString);
    }

    public static BlockState deserializeBlockState(FriendlyByteBuf buffer) {
        String locationString = buffer.readUtf();
        ResourceLocation location = new ResourceLocation(locationString);
        final Block block = ForgeRegistries.BLOCKS.getValue(location);
        if(block != null){
            return block.defaultBlockState();
        }
        return Blocks.AIR.defaultBlockState();
    }

    // Entity Type

    public static CompoundTag serializeEntityTypeNBT(CompoundTag nbt, EntityType<?> entityType) {
        nbt.putString("entity", EntityType.getKey(entityType).toString());
        return nbt;
    }

    public static EntityType<?> deserializeEntityTypeNBT(CompoundTag nbt) {
        // Prepare the resourceLocation
        String resString = nbt.getString("entity");
        if(resString.isEmpty()){return null;}
        String[] splitted = resString.split(":");
        ResourceLocation res = new ResourceLocation(splitted[0], splitted[1]);
        // Search for the entity type in the registry and return it
        return ForgeRegistries.ENTITY_TYPES.getValue(res);
    }
}