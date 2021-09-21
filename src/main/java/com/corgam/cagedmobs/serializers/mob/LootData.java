package com.corgam.cagedmobs.serializers.mob;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapedRecipe;

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
        final float chance = GsonHelper.getAsFloat(json, "chance");
        final Item item = ShapedRecipe.itemFromJson(json.getAsJsonObject("output"));
        final int min = GsonHelper.getAsInt(json, "minAmount");
        final int max = GsonHelper.getAsInt(json, "maxAmount");

        Item cookedItem = Items.AIR;
        if(json.has("output_cooked")){
            cookedItem = ShapedRecipe.itemFromJson(json.getAsJsonObject("output_cooked"));
        }

        boolean isLighting = false;
        if(json.has("lightning")){
            isLighting = GsonHelper.getAsBoolean(json, "lightning");
        }

        boolean isArrow = false;
        if(json.has("needsArrow")){
            isArrow = GsonHelper.getAsBoolean(json, "needsArrow");
        }

        int color = -1;
        if(json.has("color")){
            color = GsonHelper.getAsInt(json, "color");
        }

        return new LootData(new ItemStack(item), new ItemStack(cookedItem), chance, min, max, isLighting, isArrow, color);
    }

    public static LootData deserializeBuffer(FriendlyByteBuf buffer) {
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

    public static void serializeBuffer(FriendlyByteBuf buffer, LootData info) {
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
