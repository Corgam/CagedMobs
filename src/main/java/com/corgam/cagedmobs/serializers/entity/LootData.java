package com.corgam.cagedmobs.serializers.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class LootData {

    public static final Codec<LootData> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            Ingredient.CODEC.fieldOf("output").forGetter(LootData::getItem),
            Ingredient.CODEC.optionalFieldOf("output_cooked", Ingredient.of(Items.AIR)).forGetter(LootData::getCookedItem),
            Codec.FLOAT.fieldOf("chance").forGetter(LootData::getChance),
            Codec.INT.fieldOf("minAmount").forGetter(LootData::getMinAmount),
            Codec.INT.fieldOf("maxAmount").forGetter(LootData::getMaxAmount),
            Codec.BOOL.fieldOf("lighting").orElse(false).forGetter(LootData::isLighting),
            Codec.BOOL.fieldOf("needsArrow").orElse(false).forGetter(LootData::isArrow),
            Codec.INT.fieldOf("color").orElse(-1).forGetter(LootData::getColor),
            Codec.BOOL.fieldOf("randomDurability").orElse(false).forGetter(LootData::ifRandomDurability),
            Codec.STRING.fieldOf("nbtName").orElse("").forGetter(LootData::getNbtName),
            Codec.STRING.fieldOf("nbtData").orElse("").forGetter(LootData::getNbtData)
    ).apply(builder, LootData::new));

    private final float chance;
    private Ingredient item;
    private final Ingredient cookedItem;
    private final int minAmount;
    private final int maxAmount;
    private final boolean lighting;
    private final boolean arrow;
    private final int color;
    private final boolean randomDurability;
    private final String nbtName;
    private final String nbtData;

    public LootData(Ingredient item, Ingredient cookedItem, float chance, int min, int max, boolean lighting, boolean arrow, int color, boolean randomDurability, String nbtName, String nbtData){
        this.chance = chance;
        this.item = item;
        this.cookedItem = cookedItem;
        this.minAmount = min;
        this.maxAmount = max;
        this.lighting = lighting;
        this.arrow = arrow;
        this.color = color;
        this.randomDurability = randomDurability;
        this.nbtName = nbtName;
        this.nbtData = nbtData;
        // Check for errors
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Amounts must not be negative!");
        }
        if (min > max) {
            throw new IllegalArgumentException("Min amount must not be greater than max amount!");
        }
        // Apply NBT data
        if(!nbtName.isEmpty() && !nbtData.isEmpty()){
            ItemStack newItem = writeNBTtoItem(nbtName, nbtData, item.getItems()[0]);
            this.item = Ingredient.of(newItem);
        }
    }

    public static void serializeBuffer(FriendlyByteBuf buffer, LootData lootData) {
        // Chance
        buffer.writeFloat(lootData.getChance());
        // Item
        buffer.writeItemStack(lootData.getItem().getItems()[0], true);
        // Min amount
        buffer.writeInt(lootData.getMinAmount());
        // Max amount
        buffer.writeInt(lootData.getMaxAmount());
        // Lightning
        buffer.writeBoolean(lootData.isLighting());
        // Arrow
        buffer.writeBoolean(lootData.isArrow());
        // Cooking
        buffer.writeItemStack(lootData.getCookedItem().getItems()[0], true);
        // Color
        buffer.writeInt(lootData.getColor());
        // Random durability
        buffer.writeBoolean(lootData.ifRandomDurability());
        // NBT Name
        buffer.writeUtf(lootData.getNbtName());
        // NBT Data
        buffer.writeUtf(lootData.getNbtData());
    }

    public static LootData deserializeBuffer(FriendlyByteBuf buffer) {
        // Chance
        final float chance = buffer.readFloat();
        // Item
        final Ingredient item = Ingredient.of(buffer.readItem());
        // Min amount
        final int min = buffer.readInt();
        // Max amount
        final int max = buffer.readInt();
        // Lightning
        final boolean isLightning = buffer.readBoolean();
        // Arrow
        final boolean isArrow = buffer.readBoolean();
        // Cooked item
        final Ingredient cookedItem = Ingredient.of(buffer.readItem());
        // Color
        final int color = buffer.readInt();
        // Random durability
        final boolean randomDurability = buffer.readBoolean();
        // NBT Name
        final String nbtName = buffer.readUtf();
        // NBT Data
        final String nbtData = buffer.readUtf();

        return new LootData(item, cookedItem, chance, min, max, isLightning, isArrow, color, randomDurability, nbtName, nbtData);
    }

    /**
     * Writes NBT data to an item stack
     * @param nbtName the name of the NBT variable
     * @param nbtData the data for the NBT variable
     * @param stack the item stack to write to
     * @return the updated item stack reference
     */
    public static ItemStack writeNBTtoItem(String nbtName, String nbtData, ItemStack stack){
        CompoundTag nbt = new CompoundTag();
        nbt.putString(nbtName,nbtData);
        stack.setTag(nbt);
        return stack;
    }

    @Override
    public String toString() {
        return "Loot data - item: " + this.item.toString() + ", chance: " + this.chance + ", min: " + this.minAmount + ", max: " + this.maxAmount;
    }

    public float getChance() {
        return this.chance;
    }

    public Ingredient getItem() {
        return this.item;
    }

    public Ingredient getCookedItem() {
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
    public boolean isArrow(){
        return this.arrow;
    }

    public boolean hasColor(){
        return this.getColor() != -1;
    }

    public int getColor(){
        return this.color;
    }

    public boolean ifRandomDurability(){
        return this.randomDurability;
    }

    public String getNbtName(){
        return this.nbtName;
    }

    public String getNbtData(){
        return this.nbtData;
    }
}
