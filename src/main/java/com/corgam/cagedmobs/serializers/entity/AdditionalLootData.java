package com.corgam.cagedmobs.serializers.entity;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.registers.CagedRecipeSerializers;
import com.corgam.cagedmobs.registers.CagedRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class AdditionalLootData implements Recipe<Inventory> {

    private final ResourceLocation entityID;
    private final List<LootData> results;
    private boolean removeFromEntity;

    public AdditionalLootData(ResourceLocation id, List<LootData> results, boolean removeFromEntity){
        this.entityID = id;
        this.results = results;
        this.removeFromEntity = removeFromEntity;
        // Add the id to the list of loaded recipes
        if(id != null && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded AdditionalLootData recipe with id: " + id.toString());
        }
    }

    @Override
    public boolean matches(Inventory inv, Level worldIn) {
        return false;
    }

    @Override
    public ItemStack assemble(Inventory pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    public ResourceLocation getEntityID() {
        return this.entityID;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CagedRecipeSerializers.ADDITIONAL_LOOT_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE.get();
    }

    public List<LootData> getResults () {
        return this.results;
    }

    public @Nullable EntityType<?> getEntityType(){
        Optional<EntityType<?>> entityType = EntityType.byString(this.entityID.toString());
        return entityType.orElse(null);
    }

    public boolean isRemoveFromEntity(){
        return this.removeFromEntity;
    }

    public void setRemoveFromEntity(boolean removeFromEntity){
        this.removeFromEntity = removeFromEntity;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

}
