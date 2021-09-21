package com.corgam.cagedmobs.serializers.mob;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.setup.CagedItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.*;

public class MobData implements Recipe<Inventory> {

    public static final MobDataSerializer SERIALIZER = new MobDataSerializer();

    private final ResourceLocation id;
    private EntityType<?> entityType;
    private final Set<String> environments;
    private int growTicks;
    private final List<LootData> results;
    private int samplerTier;

    public MobData(ResourceLocation id, EntityType<?> entityType, Set<String> environments, int growTicks, List<LootData> results, int tier){
        this.id = id;
        this.environments = environments;
        this.entityType = entityType;
        this.growTicks = growTicks;
        this.results = results;
        this.samplerTier = tier;
        if(id != null && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded MobData recipe with id: " + id.toString());
        }
    }

    public EntityType<?> getEntityType(){
        return this.entityType;
    }

    public void setEntityType(EntityType<?> entityType){
        this.entityType = entityType;
    }

    public Set<String> getValidEnvs(){
        return this.environments;
    }

    @Override
    public boolean matches(Inventory inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(Inventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MobData.SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipesHelper.MOB_RECIPE;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(CagedItems.DNA_SAMPLER.get());
    }

    public int getTotalGrowTicks () {
        return this.growTicks;
    }

    public void setTotalGrowTicks(int newTicks){
        this.growTicks = newTicks;
    }

    public List<LootData> getResults () {
        return this.results;
    }

    public int getSamplerTier() {
        return samplerTier;
    }

    public void setSamplerTier(int tier){
        this.samplerTier = tier;
    }

//    @Override
//    public boolean isDynamic() {
//
//        return true;
//    }

}
