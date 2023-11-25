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

import java.util.List;
import java.util.Optional;

public class AdditionalLootData implements Recipe<Inventory> {

    private final String entityId;
    private EntityType<?> entityType;
    private final List<LootData> results;
    private boolean removeFromEntity;

    public AdditionalLootData(String entityId, List<LootData> results, boolean removeFromEntity){
        this.entityId = entityId;
        this.entityType = this.getEntityType();
        this.results = results;
        this.removeFromEntity = removeFromEntity;
        // Add the id to the list of loaded recipes
        if(!entityId.isEmpty() && CagedMobs.LOGGER != null){
            CagedMobs.LOGGER.info("Loaded AdditionalLootData recipe for entity: " + entityId);
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

    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CagedRecipeSerializers.ADDITIONAL_LOOT_RECIPE_SERIALIZER.get();
    }

    public EntityType<?> getEntityType(){
        // Try to find again entity type if it's null
        if(this.entityType == null){
            Optional<EntityType<?>> entityType = EntityType.byString(this.entityId);
            if(entityType.isPresent()) {
                this.entityType = entityType.get();
                return entityType.get();
            }
        }
        return this.entityType;
    }

    public void setEntityType(EntityType<?> entityType){
        this.entityType = entityType;
    }

    @Override
    public RecipeType<?> getType() {
        return CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE.get();
    }

    public List<LootData> getResults() {
        return this.results;
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
