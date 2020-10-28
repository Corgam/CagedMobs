package com.corgam.cagedmobs.serializers.mob;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public class LootData {

    private final float chance;
    private ItemStack item;
    private final ItemStack cookedItem;
    private final int minAmount;
    private final int maxAmount;
    private final boolean lighting;
    private int color = -1;

    LootData(ItemStack item, ItemStack cookedItem, float chance, int min, int max, boolean lighting, int color){
        this.chance = chance;
        this.item = item;
        this.cookedItem = cookedItem;
        this.minAmount = min;
        this.maxAmount = max;
        this.lighting = lighting;
        this.color = color;

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

        ItemStack cookedItem = ItemStack.EMPTY;
        if(json.has("output_cooked")){
            cookedItem = ShapedRecipe.deserializeItem(json.getAsJsonObject("output_cooked"));
        }

        boolean isLighting = false;
        if(json.has("lightning")){
            isLighting = JSONUtils.getBoolean(json, "lightning");
        }

        int color = -1;
        if(json.has("color")){
            color = JSONUtils.getInt(json, "color");
        }

        return new LootData(item, cookedItem, chance, min, max, isLighting, color);
    }

    public static LootData deserializeBuffer(PacketBuffer buffer) {
        final float chance = buffer.readFloat();
        final ItemStack item = buffer.readItemStack();
        final int min = buffer.readInt();
        final int max = buffer.readInt();
        final boolean isLightning = buffer.readBoolean();
        final ItemStack cookedItem = buffer.readItemStack();
        final int color = buffer.readInt();

        return new LootData(item, cookedItem, chance, min, max, isLightning, color);
    }

    public static void serializeBuffer(PacketBuffer buffer, LootData info) {
        buffer.writeFloat(info.getChance());
        buffer.writeItemStack(info.getItem());
        buffer.writeInt(info.getMinAmount());
        buffer.writeInt(info.getMaxAmount());
        buffer.writeBoolean(info.isLighting());
        buffer.writeItemStack(info.getCookedItem());
        buffer.writeInt(info.getColor());
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

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getCookedItem() {
        return this.cookedItem;
    }

    public int getMinAmount() {
        return this.minAmount;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public boolean isLighting(){
        return this.lighting;
    }
    public boolean isCooking(){
        return !this.cookedItem.isEmpty();
    }
    public boolean hasColor(){
        return this.getColor() != -1;
    }

    public int getColor(){
        return this.color;
    }
}
