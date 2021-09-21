package com.corgam.cagedmobs.blockEntities;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class MobCageBlockEntity extends BlockEntity{
    // Hopping and upgrades
    private boolean hopping = false;
    private boolean cooking = false;
    private boolean lightning = false;
    private boolean arrow = false;
    // Env
    private EnvironmentData environment = null;
    private ItemStack envItem = ItemStack.EMPTY;
    // Entity
    private MobData entity = null;
    private EntityType<?> entityType = null;
    // Ticks
    private int currentGrowTicks = 0;
    private int totalGrowTicks = 0;
    private boolean waitingForHarvest = false;
    // Color of entity
    private int color = 0;
    // Cached entity
    private Entity cachedEntity;
    //private WeightedSpawnerEntity renderedEntity;

    // Used to get Entity for rendering inside the cage, if cachedEntity is null, then get it from the stored EntityType
//    @OnlyIn(Dist.CLIENT)
//    public Entity getCachedEntity(Level world) {
//        if (this.cachedEntity == null) {
//            if(this.renderedEntity == null){
//                CompoundTag nbt = new CompoundTag();
//                nbt.putString("id", Registry.ENTITY_TYPE.getKey(this.entityType).toString());
//                this.renderedEntity = new WeightedSpawnerEntity(1, nbt);
//            }
//            this.cachedEntity = EntityType.loadEntityRecursive(this.renderedEntity.getTag(), world, Function.identity());
//        }
//        return this.cachedEntity;
//    }

    // METHODS

    public MobCageBlockEntity(boolean hopping) {
        super(CagedBlockEntity.MOB_CAGE.get());
        this.hopping = hopping;
    }

    public MobCageBlockEntity() {
        super(CagedBlockEntity.MOB_CAGE.get());
    }

    @Override
    public void tick() {
        //Tick only when env and mob is inside
        if(this.hasEnvAndEntity() && !waitingForHarvest) {
            // Check if ready to harvest
            if(this.currentGrowTicks >= this.getTotalGrowTicks()) {
                this.attemptHarvest();
            }else {
                // Add one tick (if entity requires waterlogging check for it)
                if(!this.entity.ifRequiresWater() || this.getBlockState().getValue(BlockStateProperties.WATERLOGGED)){
                    this.currentGrowTicks++;
                }
            }
        }
        // If has cooking upgrade spawn particles
        if(this.isCooking() && CagedMobs.CLIENT_CONFIG.shouldUpgradesParticles()){
            Random rand = new Random();
            if (!(level instanceof ServerLevel)) {
                    if (rand.nextInt(10) == 0) {
                        Level world = this.getLevel();
                        BlockPos blockpos = this.getBlockPos();
                        double d3 = (double) blockpos.getX() + world.random.nextDouble();
                        double d4 = (double) blockpos.getY() + (world.random.nextDouble()/3);
                        double d5 = (double) blockpos.getZ() + world.random.nextDouble();
                        if(!this.getBlockState().getValue(BlockStateProperties.WATERLOGGED)){
                            // If not waterlogged emit fire particles
                            world.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                            world.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                        }else{
                            // If waterlogged emit blue fire particles
                            world.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                        }

                }
            }
        }
        // If has lightning upgrade spawn particles
        if(this.isLightning() && CagedMobs.CLIENT_CONFIG.shouldUpgradesParticles()){
            Random rand = new Random();
            if (!(level instanceof ServerLevel)) {
                if (rand.nextInt(30) == 0) {
                    Level world = this.getLevel();
                    BlockPos blockpos = this.getBlockPos();
                    double d3 = (double) blockpos.getX() + 0.4 + (world.random.nextDouble()/5);
                    double d4 = (double) blockpos.getY() + 0.8;
                    double d5 = (double) blockpos.getZ() +  0.4 + (world.random.nextDouble()/5);
                    world.addParticle(ParticleTypes.END_ROD, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        // If has lightning upgrade spawn particles
        if(this.isArrow() && CagedMobs.CLIENT_CONFIG.shouldUpgradesParticles()){
            Random rand = new Random();
            if (!(level instanceof ServerLevel)) {
                if (rand.nextInt(30) == 0) {
                    Level world = this.getLevel();
                    BlockPos blockpos = this.getBlockPos();
                    double d3 = (double) blockpos.getX() + 0.4 + (world.random.nextDouble()/5);
                    double d4 = (double) blockpos.getY() + 0.8;
                    double d5 = (double) blockpos.getZ() +  0.4 + (world.random.nextDouble()/5);
                    world.addParticle(ParticleTypes.CRIT, d3, d4, d5, 0.0D, -0.5D, 0.0D);
                }
            }
        }
    }

    private boolean hasEnvAndEntity() {
        return this.hasEntity() && this.hasEnvironment();
    }

    public int getTotalGrowTicks() {
        return this.totalGrowTicks;
    }

    public int getCurrentGrowTicks() {
        return currentGrowTicks;
    }

    public float getGrowthPercentage(){
        if(this.totalGrowTicks != 0) {
            return (float) this.currentGrowTicks / this.totalGrowTicks;
        }else{
            return 0;
        }
    }

    public boolean isHopping() {
        return hopping;
    }


    public EntityType<?> getEntityType() {
        return entityType;
    }

    public ItemStack getEnvItem() {
        return envItem;
    }

///// ENVIRONMENT /////

    public void setEnvironment(ItemStack stack) {
        this.setChanged();
        this.environment = getEnvironmentFromItemStack(stack);
        // Set the env item
        ItemStack itemstack = stack.copy();
        itemstack.setCount(1);
        this.envItem = itemstack;
        // Sync with client
        if(this.level != null){
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public EnvironmentData getEnvironment() {
        return environment;
    }

    public boolean hasEnvironment() {
        return environment != null;
    }

    public static EnvironmentData getEnvironmentFromItemStack(ItemStack stack){
        EnvironmentData finalEnvData = null;
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.ENV_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof EnvironmentData) {
                final EnvironmentData envData = (EnvironmentData) recipe;
                if(envData.getInputItem().test(stack)) {
                    finalEnvData = envData;
                    break;
                }
            }
        }
        return finalEnvData;
    }

    public static boolean existsEnvironmentFromItemStack(ItemStack stack){
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.ENV_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof EnvironmentData) {
                final EnvironmentData envData = (EnvironmentData) recipe;
                if(envData.getInputItem().test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isEnvSuitable(Player player, EntityType<?> entityType, BlockState state) {
        MobData recipe = getMobDataFromType(entityType);
        // Check if entity needs waterlogged cage
        if(recipe.ifRequiresWater() && !state.getValue(BlockStateProperties.WATERLOGGED)){
            player.displayClientMessage(new TranslatableComponent("block.cagedmobs.mobcage.requiresWater").withStyle(ChatFormatting.RED), true);
            return false;
        }
        for(String env : this.environment.getEnvironments()){
            if(recipe.getValidEnvs().contains(env)){
                return true;
            }
        }
        player.displayClientMessage(new TranslatableComponent("block.cagedmobs.mobcage.envNotSuitable").withStyle(ChatFormatting.RED), true);
        return false;
    }

    public void onEnvironmentRemoval() {
        this.onEntityRemoval();
        this.environment = null;
        this.envItem = ItemStack.EMPTY;
        this.color = 0;
    }


/// ENTITY /////

    public MobData getEntity() {
        return entity;
    }

    public boolean hasEntity() {
        return entity != null;
    }

    public void setEntityFromType(EntityType<?> type, ItemStack sampler) {
        this.setChanged();
        // Lookup the entity color
        if(type.toString().contains("sheep")){
            if(sampler.hasTag() && sampler.getTag() != null && sampler.getTag().contains("Color") ){
                this.color = sampler.getTag().getInt("Color");
            }
        }
        // Load the mob data
        MobData mobData = getMobDataFromType(type);
        this.entity = mobData;
        this.entityType = type;
        // Calculate required ticks (take into account growthModifier from env)
        this.totalGrowTicks = Math.round(mobData.getTotalGrowTicks()/this.environment.getGrowModifier());
        // Sync with client
        if(this.level != null){
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public static boolean existsEntityFromType(EntityType<?> entityType) {
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                // Check for null exception
                if(mobData.getEntityType() == null){continue;}
                if(mobData.getEntityType().equals(entityType)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Used to get the MobData from entityType. Also adds all additional Loot to the entity.
    private static MobData getMobDataFromType(EntityType<?> type){
        MobData finalMobData = null;
        // Get the mobData
        for(final Recipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                // Check for null exception
                if(mobData.getEntityType() == null){continue;}
                if(mobData.getEntityType().equals(type)) {
                    finalMobData = mobData;
                    break;
                }
            }
        }
        // Add additional Loot
        if(finalMobData != null){
            for(final Recipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.ADDITIONAL_LOOT_RECIPE, RecipesHelper.getRecipeManager()).values()) {
                if(recipe instanceof AdditionalLootData) {
                    final AdditionalLootData additionalLootData = (AdditionalLootData) recipe;
                    // Check for null exception
                    if(finalMobData.getEntityType() == null){continue;}
                    if(finalMobData.getEntityType().equals(additionalLootData.getEntityType())) {
                        for(LootData data : additionalLootData.getResults()){
                            if(!finalMobData.getResults().contains(data)){
                                finalMobData.getResults().add(data);
                            }
                        }
                    }
                }
            }
        }
        return finalMobData;
    }

    public void onEntityRemoval() {
        // Entity
        this.entity = null;
        this.entityType = null;
        // Entity render
        this.cachedEntity = null;
        this.renderedEntity = null;
        // Ticks
        this.currentGrowTicks = 0;
        this.totalGrowTicks = 0;
        this.waitingForHarvest = false;
        // Color
        this.color = 0;
        // Sync with client
        if(this.level != null){
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }


////// HARVEST AND LOOT /////

    // Attempt harvest (when hopping cage and there is a inv bellow, then harvest, when not hopping, lock and wait for players interaction)
    private void attemptHarvest() {
        if(this.hopping && !CagedMobs.SERVER_CONFIG.ifHoppingCagesDisabled()) {
            // Try to auto harvest
            if(this.autoHarvest()){
                this.currentGrowTicks = 0;
            }else{
                this.currentGrowTicks = this.getTotalGrowTicks();
            }
        }else {
            // Lock
            waitingForHarvest = true;
            this.currentGrowTicks = this.getTotalGrowTicks();
        }
        // Sync with client
        if(this.level != null){
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    // Auto-harvests the cage, when there is a valid inv bellow
    private boolean autoHarvest() {
        final IItemHandler inventory = getInv(this.level, this.worldPosition.below(), Direction.UP);
        if(inventory != EmptyHandler.INSTANCE && !this.level.isClientSide()){
            // For every item in drop list
            NonNullList<ItemStack> drops =this.createDropsList();
            for(final ItemStack item : drops){
               // For every slot in inv
               for(int slot = 0; slot < inventory.getSlots(); slot++){
                   // Simulate the insert
                   if(inventory.isItemValid(slot, item) && inventory.insertItem(slot,item,true).getCount() != item.getCount()){
                    // Actual insert
                       inventory.insertItem(slot, item, false);
                       break;
                   }
               }
           }
           return true;
        }
        return false;
    }

    // Gets the ItemHandler from block
    private IItemHandler getInv(Level world, BlockPos pos, Direction side){
        final BlockEntity te = world.getBlockEntity(pos);
        // Capability system
        if(te != null){
            final LazyOptional<IItemHandler> invCap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            return invCap.orElse(EmptyHandler.INSTANCE);
        }else{
            // When block doesn't use capability system
            final BlockState state = world.getBlockState(pos);
            if(state.getBlock() instanceof ISidedInventoryProvider){
                final ISidedInventoryProvider invProvider = (ISidedInventoryProvider) state.getBlock();
                final ISidedInventory inv = invProvider.getContainer(state, world, pos);
                return new SidedInvWrapper(inv, side);
            }
        }
        return EmptyHandler.INSTANCE;
    }

    // Check if locked and waiting for player interaction
    public boolean isWaitingForHarvest() {
        return this.waitingForHarvest;
    }

    // Is run when player right clicks to harvest
    public void onPlayerHarvest() {
        if((!this.hopping || CagedMobs.SERVER_CONFIG.ifHoppingCagesDisabled()) && canPlayerHarvest()){
            this.currentGrowTicks = 0;
            this.waitingForHarvest = false;
            List<ItemStack> drops = createDropsList();
            for( ItemStack item : drops) {
                dropItem(item.copy());
            }
            this.setChanged();
            // Sync with client
            if(this.level != null){
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
    }

    // Check if a player can harvest the cage
    private boolean canPlayerHarvest(){
        return this.hasEnvAndEntity() && this.getTotalGrowTicks() > 0 && this.getCurrentGrowTicks() >= this.getTotalGrowTicks();
    }

    // Gives back a list of items that harvest will yield
    private NonNullList<ItemStack> createDropsList(){
        NonNullList<ItemStack> drops = NonNullList.create();
        List<Item> blacklistedItems = RecipesHelper.getItemsFromConfigList();
        for(LootData loot : this.entity.getResults()) {
            // Skip item if it's blacklisted or whitelisted
            if(!CagedMobs.SERVER_CONFIG.isEntitiesListInWhitelistMode()){
                if(blacklistedItems.contains(loot.getItem().getItem())){continue;}
            }else{
                if(!blacklistedItems.contains(loot.getItem().getItem())){continue;}
            }
            // Choose a loot type for entity's color
            if(loot.getColor() != -1){
                if(loot.getColor() != this.color){
                    continue;
                }
            }
            // Skip if loot needs lightning upgrade, but it's not present in the cage.
            if(!this.isLightning() && loot.isLighting()){
                continue;
            }
            // Skip if loot needs arrow upgrade, but it's not present in the cage.
            if(!this.isArrow() && loot.isArrow()){
                continue;
            }
            if(this.level != null && this.level.random.nextFloat() <= loot.getChance()) {
                // Roll the amount of items
                int range = loot.getMaxAmount() - loot.getMinAmount() + 1;
                int amount = this.level.random.nextInt(range) + loot.getMinAmount();
                if(amount > 0) {
                    // Add copied item stack to the drop list
                    ItemStack stack = loot.getItem().copy();
                    // Replace the item if there is a cooking upgrade
                    if(this.isCooking() && loot.isCooking()){
                        stack = loot.getCookedItem().copy();
                    }
                    stack.setCount(amount);
                    drops.add(stack);
                }
            }
        }
        return drops;
    }

    // Creates an item entity
    public void dropItem(ItemStack item) {
        if(this.level != null && !this.level.isClientSide) {
            final double offsetX = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
            final double offsetY = (double) (level.random.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
            final double offsetZ = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
            final ItemEntity itemEntity = new ItemEntity(this.level, this.worldPosition.getX() + offsetX, this.worldPosition.getY() + offsetY, this.worldPosition.getZ() + offsetZ, item);
            itemEntity.setDefaultPickUpDelay();
            this.level.addFreshEntity(itemEntity);
        }
    }

    // NETWORKING

    @Override
    public CompoundTag getUpdateTag(){
        CompoundTag tag = super.getUpdateTag();
        // Put upgrades
        tag.putBoolean("cooking",this.cooking);
        tag.putBoolean("lightning", this.lightning);
        tag.putBoolean("arrow",this.arrow);
        if(this.hasEnvironment()) {
            // Put env info
            tag.put("environmentItem", this.envItem.serializeNBT());
            // If cage has entity, put entity info
            if(this.hasEntity()){
                // Put entity type
                SerializationHelper.serializeEntityTypeNBT(tag, this.entityType);
                // Put color
                tag.putInt("color",this.color);
                // Put ticks info
                tag.putInt("currentGrowTicks", this.currentGrowTicks);
                tag.putBoolean("waitingForHarvest", this.waitingForHarvest);
            }
        }
        return tag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket(){
        return new SUpdateTileEntityPacket(this.worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet){
        CompoundTag tag = packet.getTag();
        // Store old env and entity type
        ItemStack oldEnv = this.envItem;
        EntityType<?> oldEntityType = this.entityType;
        // Get upgrades
        this.cooking = tag.getBoolean("cooking");
        this.lightning = tag.getBoolean("lightning");
        this.arrow = tag.getBoolean("arrow");
        // Read the env
        this.envItem = ItemStack.of(tag.getCompound("environmentItem"));
        this.environment = MobCageBlockEntity.getEnvironmentFromItemStack(this.envItem);
        // Read the mob data
        this.entityType = SerializationHelper.deserializeEntityTypeNBT(tag);
        this.entity = MobCageBlockEntity.getMobDataFromType(this.entityType);
        if(this.entityType == null){
            this.renderedEntity = null;
            this.cachedEntity = null;
        }
        // Read color
        this.color = tag.getInt("color");
        // Read ticks info
        this.waitingForHarvest = tag.getBoolean("waitingForHarvest");
        this.currentGrowTicks = tag.getInt("currentGrowTicks");
        if(hasEntity()){
            this.totalGrowTicks = Math.round( this.entity.getTotalGrowTicks()/this.environment.getGrowModifier());
        }
        // If env or entity changed, refresh model data
        if(!Objects.equals(oldEnv, this.envItem) || !Objects.equals(oldEntityType,this.entityType)){
            ModelDataManager.requestModelDataRefresh(this);
            // Sync with client
            if(this.level != null){
                this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }
    }

    /// SERIALIZATION ///

    // Deserialize the block to read it from the drive
    @Override
    public void load(BlockState state, CompoundTag nbt) {
        // Call the parent
        super.load(state, nbt);
        // Read hopping and upgrades
        this.hopping = nbt.getBoolean("hopping");
        this.cooking = nbt.getBoolean("cooking");
        this.lightning = nbt.getBoolean("lightning");
        this.arrow = nbt.getBoolean("arrow");
        // Read the env
        this.envItem = ItemStack.of(nbt.getCompound("environmentItem"));
        this.environment = MobCageBlockEntity.getEnvironmentFromItemStack(this.envItem);
        // Read the mob data
        this.entityType = SerializationHelper.deserializeEntityTypeNBT(nbt);
        this.entity = MobCageBlockEntity.getMobDataFromType(this.entityType);
        // Read color
        this.color = nbt.getInt("color");
        // Read ticks info
        this.waitingForHarvest = nbt.getBoolean("waitingForHarvest");
        this.currentGrowTicks = nbt.getInt("currentGrowTicks");
        if(hasEnvAndEntity()){
            this.totalGrowTicks = Math.round( this.entity.getTotalGrowTicks()/this.environment.getGrowModifier());
        }
    }

    // Serialize the block to save it on drive
    @Override
    public CompoundTag save(CompoundTag dataTag) {
        // Put hopping and upgrades
        dataTag.putBoolean("hopping", this.hopping);
        dataTag.putBoolean("cooking", this.cooking);
        dataTag.putBoolean("lightning", this.lightning);
        dataTag.putBoolean("arrow", this.arrow);
        // If cage has env, then put env info and maybe entity info
        if(this.hasEnvironment()) {
            // Put env info
            dataTag.put("environmentItem", this.envItem.serializeNBT());
            // If cage has entity, put entity info
            if(this.hasEntity()){
                // Put entity type
                SerializationHelper.serializeEntityTypeNBT(dataTag, this.entityType);
                // Put color
                dataTag.putInt("color", this.color);
                // Put ticks info
                dataTag.putInt("currentGrowTicks", this.currentGrowTicks);
                dataTag.putBoolean("waitingForHarvest", this.waitingForHarvest);
            }
        }
        return super.save(dataTag);
    }

    public boolean isCooking() {
        return this.cooking;
    }

    public boolean isLightning() {
        return this.lightning;
    }

    public boolean isArrow(){
        return this.arrow;
    }

    public boolean hasUpgrade(){
        return this.isArrow() || this.isCooking() || this.isLightning();
    }


    public int getColor(){
        return this.color;
    }

    public void setCooking(boolean cooking) {
        this.cooking = cooking;
        // Sync with client
        if(this.level != null){
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public void setLightning(boolean lightning) {
        this.lightning = lightning;
        // Sync with client
        if(this.level != null){
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public void setArrow(boolean arrow){
        this.arrow = arrow;
        // Sync with client
        if(this.level != null){
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }
}
