package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.setup.CagedItems;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.*;

public class MobData implements IRecipe<IInventory> {

    public static final MobDataSerializer SERIALIZER = new MobDataSerializer();

    private final ResourceLocation id;
    private EntityType<?> entityType;
    private Set<String> environments;
    private int growTicks;
    private List<LootData> results;

    MobData(ResourceLocation id, EntityType<?> entityType, Set<String> environments, int growTicks, List<LootData> results){
        this.id = id;
        this.environments = environments;
        this.entityType = entityType;
        this.growTicks = growTicks;
        this.results = results;
        System.out.println("Loaded MobData recipe for: " + this.entityType.toString());
    }

    public EntityType<?> getEntityType(){
        return this.entityType;
    }

    public Set<String> getValidEnvs(){
        return this.environments;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return MobData.SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return CagedMobs.MOB_RECIPE;
    }

    @Override
    public ItemStack getIcon () {
        return new ItemStack(CagedItems.DNA_SAMPLER.get());
    }

    public int getTotalGrowTicks () {
        return this.growTicks;
    }

    public List<LootData> getResults () {
        return this.results;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("id", this.id.toString());
        nbt.putString("entityType", this.entityType.toString());
        nbt.putString("environments", this.environments.toString());
        nbt.putInt("growTicks", this.growTicks);
        nbt.putInt("resultsSize", this.results.size());
        int i = 0;
        for(LootData data : this.results) {
            nbt.put("data" + i, data.serializeNBT());
            i++;
        }
        return nbt;
    }


}
