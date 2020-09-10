package com.corgam.cagedmobs.serializers.mob;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public class LootData {

    private final float chance;
    private final ItemStack item;
    private final int minAmount;
    private final int maxAmount;

    LootData(ItemStack item, float chance, int min, int max){
        this.chance = chance;
        this.item = item;
        this.minAmount = min;
        this.maxAmount = max;

        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Amounts must not be negative!");
        }
        if (min > max) {
            throw new IllegalArgumentException("Min amount must not be greater than max amount!");
        }
    }

    public static LootData deserialize(JsonObject json) {
        final float chance = JSONUtils.getFloat(json, "chance");
        final ItemStack item = ShapedRecipe.deserializeItem(json.getAsJsonObject("output"));
        final int min = JSONUtils.getInt(json, "minAmount");
        final int max = JSONUtils.getInt(json, "maxAmount");

        return new LootData(item, chance, min, max);
    }

    public static LootData deserializeBuffer(PacketBuffer buffer) {
        final float chance = buffer.readFloat();
        final ItemStack item = buffer.readItemStack();
        final int min = buffer.readInt();
        final int max = buffer.readInt();

        return new LootData(item, chance, min, max);
    }

    public static void serializeBuffer(PacketBuffer buffer, LootData info) {
        buffer.writeFloat(info.getChance());
        buffer.writeItemStack(info.getItem());
        buffer.writeInt(info.getMinAmount());
        buffer.writeInt(info.getMaxAmount());
    }

    @Override
    public String toString() {
        return "Loot data - item: " + this.item.toString() + ", chance: " + this.chance + ", min: " + this.minAmount + ", max: " + this.maxAmount;
    }

    public float getChance() {
        return this.chance;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public int getMinAmount() {
        return this.minAmount;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public CompoundNBT serializeNBT() {
        // TODO Auto-generated method stub
        return null;
    }
}
