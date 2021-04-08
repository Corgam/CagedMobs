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
    private final boolean arrow;
    private int color;

    public LootData(ItemStack item, ItemStack cookedItem, float chance, int min, int max, boolean lighting, boolean arrow, int color){
        this.chance = chance;
        this.item = item;
        this.cookedItem = cookedItem;
        this.minAmount = min;
        this.maxAmount = max;
        this.lighting = lighting;
        this.arrow = arrow;
        this.color = color;

        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Amounts must not be negative!");
        }
        if (min > max) {
            throw new IllegalArgumentException("Min amount must not be greater than max amount!");
        }
    }

    public static LootData deserialize(JsonObject json) {
        final float chance = JSONUtils.getAsFloat(json, "chance");
        final ItemStack item = ShapedRecipe.itemFromJson(json.getAsJsonObject("output"));
        final int min = JSONUtils.getAsInt(json, "minAmount");
        final int max = JSONUtils.getAsInt(json, "maxAmount");

        ItemStack cookedItem = ItemStack.EMPTY;
        if(json.has("output_cooked")){
            cookedItem = ShapedRecipe.itemFromJson(json.getAsJsonObject("output_cooked"));
        }

        boolean isLighting = false;
        if(json.has("lightning")){
            isLighting = JSONUtils.getAsBoolean(json, "lightning");
        }

        boolean isArrow = false;
        if(json.has("needsArrow")){
            isArrow = JSONUtils.getAsBoolean(json, "needsArrow");
        }

        int color = -1;
        if(json.has("color")){
            color = JSONUtils.getAsInt(json, "color");
        }

        return new LootData(item, cookedItem, chance, min, max, isLighting, isArrow, color);
    }

    public static LootData deserializeBuffer(PacketBuffer buffer) {
        // Chance
        final float chance = buffer.readFloat();
        // Item
        final ItemStack item = buffer.readItem();
        // Min amount
        final int min = buffer.readInt();
        // Max amount
        final int max = buffer.readInt();
        // Lightning
        final boolean isLightning = buffer.readBoolean();
        // Arrow
        final boolean isArrow = buffer.readBoolean();
        // Cooked item
        final ItemStack cookedItem = buffer.readItem();
        // Color
        final int color = buffer.readInt();

        return new LootData(item, cookedItem, chance, min, max, isLightning, isArrow, color);
    }

    public static void serializeBuffer(PacketBuffer buffer, LootData info) {
        // Chance
        buffer.writeFloat(info.getChance());
        // Item
        buffer.writeItemStack(info.getItem(),true);
        // Min amount
        buffer.writeInt(info.getMinAmount());
        // Max amount
        buffer.writeInt(info.getMaxAmount());
        // Lightning
        buffer.writeBoolean(info.isLighting());
        // Arrow
        buffer.writeBoolean(info.isArrow());
        // Cooking
        buffer.writeItemStack(info.getCookedItem(),true);
        // Color
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
    public boolean isArrow(){return this.arrow;}

    public boolean hasColor(){
        return this.getColor() != -1;
    }

    public int getColor(){
        return this.color;
    }

}
