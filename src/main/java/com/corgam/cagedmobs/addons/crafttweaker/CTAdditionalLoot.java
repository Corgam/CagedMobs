package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.corgam.cagedmobs.serializers.entity.AdditionalLootData;
import com.corgam.cagedmobs.serializers.entity.LootData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.AdditionalLoot")
public class CTAdditionalLoot {

    private final AdditionalLootData data;

    public CTAdditionalLoot(String id, EntityType<?> entityType, Boolean removeFromEntity){
        this(new AdditionalLootData(ResourceLocation.tryParse(id), entityType, new ArrayList<>(), removeFromEntity));
    }

    public CTAdditionalLoot(AdditionalLootData lootData){
        this.data = lootData;
    }

    @ZenCodeType.Method
    public CTAdditionalLoot addLoot(IItemStack item, float chance){
        return this.addLoot(item, chance, 1, 1);
    }

    @ZenCodeType.Method
    public CTAdditionalLoot addLoot(IItemStack item, float chance, int min, int max){
        return this.addLoot(item, null, chance, min, max, false, false, -1, false);
    }

    @ZenCodeType.Method
    public CTAdditionalLoot addLoot(IItemStack item, IItemStack cookedItem, float chance, int min, int max){
        return this.addLoot(item, cookedItem, chance, min, max, false, false, -1, false);
    }

    @ZenCodeType.Method
    public CTAdditionalLoot addLoot(IItemStack item, float chance, int min, int max, boolean lightning, boolean arrow){
        return this.addLoot(item, null, chance, min, max, lightning, arrow, -1, false);
    }

    @ZenCodeType.Method
    public CTAdditionalLoot addLoot(IItemStack item, IItemStack cookedItem, float chance, int min, int max, boolean lighting, boolean arrow, int color, boolean randomDurability){
        // To prevent adding the same item twice, look if it's already there
        for(LootData loot : this.data.getResults()){
            if(loot.getItem().equals(item.getInternal(),false)){
                return this;
            }
        }
        // If there is a cooked variant
        if(cookedItem == null || cookedItem.getInternal().getItem().equals(Items.AIR)){
            this.data.getResults().add(new LootData(item.getInternal(), ItemStack.EMPTY, chance, min, max, lighting, arrow, color, randomDurability));
        }else{
            this.data.getResults().add(new LootData(item.getInternal(), cookedItem.getInternal(), chance, min, max, lighting, arrow, color, randomDurability));
        }
        return this;
    }

    @ZenCodeType.Method
    public CTAdditionalLoot clearLoot(){
        this.data.getResults().clear();
        return this;
    }

    @ZenCodeType.Method
    public CTAdditionalLoot removeLoot(IIngredient remove){
        final Ingredient ing = remove.asVanillaIngredient();
        this.data.getResults().removeIf(drop -> ing.test(drop.getItem()));
        return this;
    }

    @ZenCodeType.Method
    public CTAdditionalLoot setEntityType(EntityType<?> entityType) {
        this.data.setEntityType(entityType);
        return this;
    }

    @ZenCodeType.Method
    public CTAdditionalLoot setRemoveFromEntity(boolean removeFromEntity) {
        this.data.setRemoveFromEntity(removeFromEntity);
        return this;
    }

    public AdditionalLootData getAdditionalLootData () {
        return this.data;
    }


}