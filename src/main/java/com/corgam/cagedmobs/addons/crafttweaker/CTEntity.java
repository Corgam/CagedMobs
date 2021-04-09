package com.corgam.cagedmobs.addons.crafttweaker;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@ZenRegister
@ZenCodeType.Name("mods.cagedmobs.Entity")
public class CTEntity {

    private final MobData data;

    public CTEntity(String id, MCEntityType entityType, int growTicks, int tier, String[] environments){
        this(new MobData(ResourceLocation.tryParse(id),entityType.getInternal(),new HashSet<>(Arrays.asList(environments)),growTicks,new ArrayList<>(),tier));
    }

    public CTEntity(MobData mobData){
        this.data = mobData;
    }

    @ZenCodeType.Method
    public CTEntity addEnvironment(String env){
        this.data.getValidEnvs().add(env);
        return this;
    }

    @ZenCodeType.Method
    public CTEntity removeEnvironment(String env){
        this.data.getValidEnvs().remove(env);
        return this;
    }

    @ZenCodeType.Method
    public CTEntity clearEnvironments(){
        this.data.getValidEnvs().clear();
        return this;
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, float chance){
        return this.addLoot(item, chance, 1, 1);
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, float chance, int min, int max){
        return this.addLoot(item, null, chance, min, max, false, false, -1);
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, IItemStack cookedItem, float chance, int min, int max){
        return this.addLoot(item, cookedItem, chance, min, max, false, false, -1);
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, float chance, int min, int max, boolean lightning, boolean arrow){
        return this.addLoot(item, null, chance, min, max, lightning, arrow, -1);
    }

    @ZenCodeType.Method
    public CTEntity addLoot(IItemStack item, IItemStack cookedItem, float chance, int min, int max, boolean lighting, boolean arrow, int color){
        // To prevent adding the same item twice, look if it's already there
        for(LootData loot : this.data.getResults()){
            if(loot.getItem().equals(item.getInternal(),false)){
                return this;
            }
        }
        // If there is a cooked variant
        if(cookedItem == null || cookedItem.getInternal().getItem().equals(Items.AIR)){
            this.data.getResults().add(new LootData(item.getInternal(), ItemStack.EMPTY, chance, min, max, lighting, arrow, color));
        }else{
            this.data.getResults().add(new LootData(item.getInternal(), cookedItem.getInternal(), chance, min, max, lighting, arrow, color));
        }
        return this;
    }

    @ZenCodeType.Method
    public CTEntity clearLoot(){
        this.data.getResults().clear();
        return this;
    }

    @ZenCodeType.Method
    public CTEntity removeLoot(IIngredient remove){
        final Ingredient ing = remove.asVanillaIngredient();
        this.data.getResults().removeIf(drop -> ing.test(drop.getItem()));
        return this;
    }

    @ZenCodeType.Method
    public CTEntity setGrowthTicks(int ticks) {
        this.data.setTotalGrowTicks(ticks);
        return this;
    }

    @ZenCodeType.Method
    public CTEntity setEntityType(MCEntityType entityType) {
        this.data.setEntityType(entityType.getInternal());
        return this;
    }

    @ZenCodeType.Method
    public CTEntity setTier(int tier) {
        this.data.setSamplerTier(tier);
        return this;
    }

    public MobData getMobData () {
        return this.data;
    }


}
