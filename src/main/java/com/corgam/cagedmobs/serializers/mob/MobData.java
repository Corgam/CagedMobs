package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.setup.CagedItems;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.*;

public class MobData implements IRecipe<IInventory> {

    public static final MobDataSerializer SERIALIZER = new MobDataSerializer();

    private final ResourceLocation id;
    private final EntityType<?> entityType;
    private final Set<String> environments;
    private final int growTicks;
    private final List<LootData> results;
    private final int samplerTier;

    MobData(ResourceLocation id, EntityType<?> entityType, Set<String> environments, int growTicks, List<LootData> results, int tier){
        this.id = id;
        this.environments = environments;
        this.entityType = entityType;
        this.growTicks = growTicks;
        this.results = results;
        this.samplerTier = tier;
        CagedMobs.LOGGER.info("Loaded MobData recipe for: " + this.entityType.toString());
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
        return RecipesHelper.MOB_RECIPE;
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

    public int getSamplerTier() {
        return samplerTier;
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

    public static MobData deserializeNBT(CompoundNBT nbt){
        return null;
    }


}
