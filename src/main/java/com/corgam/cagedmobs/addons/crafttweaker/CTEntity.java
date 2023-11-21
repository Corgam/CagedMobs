package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.corgam.cagedmobs.serializers.entity.EntityData;
import com.corgam.cagedmobs.serializers.entity.LootData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.Entity")
public class CTEntity {

    private final EntityData entityData;

    public CTEntity(String id, EntityType<?> entityType, int growTicks, boolean requiresWater, int tier, String[] environments){
        this(new EntityData(ResourceLocation.tryParse(id),entityType,new HashSet<>(Arrays.asList(environments)),growTicks, requiresWater, new ArrayList<>(),tier));
    }

    public CTEntity(EntityData entityData){
        this.entityData = entityData;
    }

    @ZenCodeType.Method
    public CTEntity addEnvironment(String env){
        this.entityData.getValidEnvs().add(env);
        return this;
    }

    @ZenCodeType.Method
    public CTEntity removeEnvironment(String env){
        this.entityData.getValidEnvs().remove(env);
        return this;
    }

    @ZenCodeType.Method
    public CTEntity clearEnvironments(){
        this.entityData.getValidEnvs().clear();
        return this;
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, float chance){
        return this.addLoot(item, chance, 1, 1);
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, float chance, int min, int max){
        return this.addLoot(item, null, chance, min, max, false, false, -1, false);
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, IItemStack cookedItem, float chance, int min, int max){
        return this.addLoot(item, cookedItem, chance, min, max, false, false, -1, false);
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, float chance, int min, int max, boolean lightning, boolean arrow){
        return this.addLoot(item, null, chance, min, max, lightning, arrow, -1, false);
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, IItemStack cookedItem, float chance, int min, int max, boolean lighting, boolean arrow, int color, boolean randomDurability){
        // To prevent adding the same item twice, look if it's already there
        for(LootData loot : this.entityData.getResults()){
            if(loot.getItem().equals(item.getInternal(),false)){
                return this;
            }
        }
        // If there is a cooked variant
        if(cookedItem == null || cookedItem.getInternal().getItem().equals(Items.AIR)){
            this.entityData.getResults().add(new LootData(item.getInternal(), ItemStack.EMPTY, chance, min, max, lighting, arrow, color, randomDurability));
        }else{
            this.entityData.getResults().add(new LootData(item.getInternal(), cookedItem.getInternal(), chance, min, max, lighting, arrow, color, randomDurability));
        }
        return this;
    }

    @ZenCodeType.Method
    public CTEntity clearLoot(){
        this.entityData.getResults().clear();
        return this;
    }

    @ZenCodeType.Method
    public CTEntity removeLoot(IIngredient remove){
        final Ingredient ing = remove.asVanillaIngredient();
        this.entityData.getResults().removeIf(drop -> ing.test(drop.getItem()));
        return this;
    }

    @ZenCodeType.Method
    public CTEntity setGrowthTicks(int ticks) {
        this.entityData.setTotalGrowTicks(ticks);
        return this;
    }

    @ZenCodeType.Method
    public CTEntity setEntityType(String entityId) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(entityId));
        this.entityData.setEntityType(entityType);
        return this;
    }

    @ZenCodeType.Method
    public CTEntity setTier(int tier) {
        this.entityData.setSamplerTier(tier);
        return this;
    }

    @ZenCodeType.Method
    public CTEntity setIfRequiresWater(boolean requiresWater) {
        this.entityData.setIfRequiresWater(requiresWater);
        return this;
    }

    public EntityData getEntityData() {
        return this.entityData;
    }


}
