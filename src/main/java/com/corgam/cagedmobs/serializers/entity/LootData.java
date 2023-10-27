package com.corgam.cagedmobs.serializers.entity;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class LootData {

    public static final Codec<LootData> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            ItemStack.CODEC.fieldOf("item").forGetter(LootData::getItem),
            ItemStack.CODEC.fieldOf("cookedItem").forGetter(LootData::getCookedItem),
            Codec.FLOAT.fieldOf("chance").forGetter(LootData::getChance),
            Codec.INT.fieldOf("minAmount").forGetter(LootData::getMinAmount),
            Codec.INT.fieldOf("maxAmount").forGetter(LootData::getMaxAmount),
            Codec.BOOL.fieldOf("lighting").orElse(false).forGetter(LootData::isLighting),
            Codec.BOOL.fieldOf("needsArrow").orElse(false).forGetter(LootData::isArrow),
            Codec.INT.fieldOf("color").orElse(-1).forGetter(LootData::getColor),
            Codec.BOOL.fieldOf("randomDurability").orElse(false).forGetter(LootData::ifRandomDurability)
    ).apply(builder, LootData::new));

    private final float chance;
    private ItemStack item;
    private ItemStack cookedItem;
    private final int minAmount;
    private final int maxAmount;
    private final boolean lighting;
    private final boolean arrow;
    private final int color;
    private final boolean randomDurability;

    public LootData(ItemStack item, ItemStack cookedItem, float chance, int min, int max, boolean lighting, boolean arrow, int color, boolean randomDurability){
        this.chance = chance;
        this.item = item;
        this.cookedItem = cookedItem;
        this.minAmount = min;
        this.maxAmount = max;
        this.lighting = lighting;
        this.arrow = arrow;
        this.color = color;
        this.randomDurability = randomDurability;

        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Amounts must not be negative!");
        }
        if (min > max) {
            throw new IllegalArgumentException("Min amount must not be greater than max amount!");
        }
    }

    public static LootData deserialize(JsonObject json) {
        final float chance = GsonHelper.getAsFloat(json, "chance");
        final Item item = GsonHelper.getAsItem(json, "output").get();
        final int min = GsonHelper.getAsInt(json, "minAmount");
        final int max = GsonHelper.getAsInt(json, "maxAmount");
        // Cooked item
        Item cookedItem = Items.AIR;
        if(json.has("output_cooked")){
            cookedItem = GsonHelper.getAsItem(json, "output_cooked").get();
        }
        // Requires lightning upgrade
        boolean isLighting = false;
        if(json.has("lightning")){
            isLighting = GsonHelper.getAsBoolean(json, "lightning");
        }
        // Requires arrow upgrade
        boolean isArrow = false;
        if(json.has("needsArrow")){
            isArrow = GsonHelper.getAsBoolean(json, "needsArrow");
        }
        // Color NBT
        int color = -1;
        if(json.has("color")){
            color = GsonHelper.getAsInt(json, "color");
        }
        // Random Durability
        boolean randomDurability = false;
        if(json.has("randomDurability")){
            randomDurability = GsonHelper.getAsBoolean(json, "randomDurability");
        }
        return new LootData(new ItemStack(item), new ItemStack(cookedItem), chance, min, max, isLighting, isArrow, color, randomDurability);
    }

    public static void serializeBuffer(FriendlyByteBuf buffer, LootData lootData) {
        // Chance
        buffer.writeFloat(lootData.getChance());
        // Item
        buffer.writeItemStack(lootData.getItem(),true);
        // Min amount
        buffer.writeInt(lootData.getMinAmount());
        // Max amount
        buffer.writeInt(lootData.getMaxAmount());
        // Lightning
        buffer.writeBoolean(lootData.isLighting());
        // Arrow
        buffer.writeBoolean(lootData.isArrow());
        // Cooking
        buffer.writeItemStack(lootData.getCookedItem(),true);
        // Color
        buffer.writeInt(lootData.getColor());
        // Random durability
        buffer.writeBoolean(lootData.randomDurability);
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
        // Random durability
        final boolean randomDurability = buffer.readBoolean();

        return new LootData(item, cookedItem, chance, min, max, isLightning, isArrow, color, randomDurability);
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

    public void setCookedItem(ItemStack item){
        this.cookedItem = item;
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

    public boolean ifRandomDurability(){return this.randomDurability;}

}
