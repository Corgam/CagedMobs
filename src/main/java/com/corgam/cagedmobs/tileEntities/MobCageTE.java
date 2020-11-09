package com.corgam.cagedmobs.tileEntities;

import com.corgam.cagedmobs.CagedMobs;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedTE;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

public class MobCageTE extends TileEntity implements ITickableTileEntity {
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

    private Entity cachedEntity;
    private WeightedSpawnerEntity renderedEntity;

    // Used to get Entity for rendering inside the cage, if cachedEntity is null, then get it from the stored EntityType
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Entity getCachedEntity() {
        if (this.cachedEntity == null) {
            if(this.renderedEntity == null){
                CompoundNBT nbt = new CompoundNBT();
                nbt.putString("id", Registry.ENTITY_TYPE.getKey(this.entityType).toString());
                this.renderedEntity = new WeightedSpawnerEntity(1, nbt);
            }
            if(Minecraft.getInstance().getIntegratedServer() != null) {
                try{
                    this.cachedEntity = EntityType.loadEntityAndExecute(this.renderedEntity.getNbt(), Minecraft.getInstance().getIntegratedServer().getWorlds().iterator().next(), Function.identity());
                }catch(Exception e){
                    CagedMobs.LOGGER.error("Error getting cached entity!");
                }
            }
        }
        return this.cachedEntity;
    }

    // METHODS

    public MobCageTE(boolean hopping) {
        super(CagedTE.MOB_CAGE.get());
        this.hopping = hopping;
    }

    public MobCageTE() {
        super(CagedTE.MOB_CAGE.get());
    }

    @Override
    public void tick() {
        //Tick only when env and mob is inside
        if(this.hasEnvAndEntity() && !waitingForHarvest) {
            // Check if ready to harvest
            if(this.currentGrowTicks >= this.getTotalGrowTicks()) {
                this.attemptHarvest();
            }else {
                // Add one tick
                this.currentGrowTicks++;
            }
        }
        // If has cooking upgrade spawn particles
        if(this.isCooking() && CagedMobs.CLIENT_CONFIG.shouldUpgradesParticles()){
            Random rand = new Random();
            if (!(world instanceof ServerWorld)) {
                    if (rand.nextInt(10) == 0) {
                        World world = this.getWorld();
                        BlockPos blockpos = this.getPos();
                        double d3 = (double) blockpos.getX() + world.rand.nextDouble();
                        double d4 = (double) blockpos.getY() + (world.rand.nextDouble()/3);
                        double d5 = (double) blockpos.getZ() + world.rand.nextDouble();
                        world.addParticle(ParticleTypes.SMOKE, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                        world.addParticle(ParticleTypes.FLAME, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        // If has lightning upgrade spawn particles
        if(this.isLightning() && CagedMobs.CLIENT_CONFIG.shouldUpgradesParticles()){
            Random rand = new Random();
            if (!(world instanceof ServerWorld)) {
                if (rand.nextInt(30) == 0) {
                    World world = this.getWorld();
                    BlockPos blockpos = this.getPos();
                    double d3 = (double) blockpos.getX() + 0.4 + (world.rand.nextDouble()/5);
                    double d4 = (double) blockpos.getY() + 0.8;
                    double d5 = (double) blockpos.getZ() +  0.4 + (world.rand.nextDouble()/5);
                    world.addParticle(ParticleTypes.END_ROD, d3, d4, d5, 0.0D, 0.0D, 0.0D);
                }
            }
        }
        // If has lightning upgrade spawn particles
        if(this.isArrow() && CagedMobs.CLIENT_CONFIG.shouldUpgradesParticles()){
            Random rand = new Random();
            if (!(world instanceof ServerWorld)) {
                if (rand.nextInt(30) == 0) {
                    World world = this.getWorld();
                    BlockPos blockpos = this.getPos();
                    double d3 = (double) blockpos.getX() + 0.4 + (world.rand.nextDouble()/5);
                    double d4 = (double) blockpos.getY() + 0.8;
                    double d5 = (double) blockpos.getZ() +  0.4 + (world.rand.nextDouble()/5);
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
        this.markDirty();
        this.environment = getEnvironmentFromItemStack(stack);
        // Set the env item
        ItemStack itemstack = stack.copy();
        itemstack.setCount(1);
        this.envItem = itemstack;
        // Sync with client
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public EnvironmentData getEnvironment() {
        return environment;
    }

    public boolean hasEnvironment() {
        return environment != null;
    }

    private static EnvironmentData getEnvironmentFromItemStack(ItemStack stack){
        EnvironmentData finalEnvData = null;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.ENV_RECIPE, RecipesHelper.getRecipeManager()).values()) {
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
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.ENV_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof EnvironmentData) {
                final EnvironmentData envData = (EnvironmentData) recipe;
                if(envData.getInputItem().test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isEnvSuitable(PlayerEntity player, EntityType<?> entityType) {
        MobData recipe = getMobDataFromType(entityType);
        for(String env : this.environment.getEnvironments()){
            if(recipe.getValidEnvs().contains(env)){
                return true;
            }
        }
        player.sendStatusMessage(new TranslationTextComponent("block.cagedmobs.mobcage.envNotSuitable"), true);
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
        this.markDirty();
        // Lookup the entity color
        if(Objects.equals(type.getRegistryName(), ResourceLocation.tryCreate("sheep"))){
            if(sampler.hasTag() && sampler.getTag() != null){
                this.color = sampler.getTag().getInt("Color");
            }

        }
        // Load the mob data
        MobData mobData = getMobDataFromType(type);
        this.entity = mobData;
        this.entityType = type;
        this.totalGrowTicks = mobData.getTotalGrowTicks();
        // Sync with client
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    public static boolean existsEntityFromType(EntityType<?> entityType) {
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                if(mobData.getEntityType().equals(entityType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static MobData getMobDataFromType(EntityType<?> type){
        MobData finalMobData = null;
        for(final IRecipe<?> recipe : RecipesHelper.getRecipes(RecipesHelper.MOB_RECIPE, RecipesHelper.getRecipeManager()).values()) {
            if(recipe instanceof MobData) {
                final MobData mobData = (MobData) recipe;
                if(mobData.getEntityType().equals(type)) {
                    finalMobData = mobData;
                    break;
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
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }


////// HARVEST AND LOOT /////

    // Attempt harvest (when hopping cage and there is a inv bellow, then harvest, when not hopping, lock and wait for players interaction)
    private void attemptHarvest() {
        if(this.hopping) {
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
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }

    // Auto-harvests the cage, when there is a valid inv bellow
    private boolean autoHarvest() {
        final IItemHandler inventory = getInv(this.world, this.pos.down(), Direction.UP);
        if(inventory != EmptyHandler.INSTANCE && !this.world.isRemote){
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
    private IItemHandler getInv(World world, BlockPos pos, Direction side){
        final TileEntity te = world.getTileEntity(pos);
        // Capability system
        if(te != null){
            final LazyOptional<IItemHandler> invCap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            return invCap.orElse(EmptyHandler.INSTANCE);
        }else{
            // When block doesn't use capability system
            final BlockState state = world.getBlockState(pos);
            if(state.getBlock() instanceof ISidedInventoryProvider){
                final ISidedInventoryProvider invProvider = (ISidedInventoryProvider) state.getBlock();
                final ISidedInventory inv = invProvider.createInventory(state, world, pos);
                if(inv != null){
                    return new SidedInvWrapper(inv, side);
                }
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
        if(!this.hopping && canPlayerHarvest()){
            this.currentGrowTicks = 0;
            this.waitingForHarvest = false;
            List<ItemStack> drops = createDropsList();
            for( ItemStack item : drops) {
                dropItem(item.copy());
            }
            this.markDirty();
            // Sync with client
            this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    // Check if a player can harvest the cage
    private boolean canPlayerHarvest(){
        return this.hasEnvAndEntity() && this.getTotalGrowTicks() > 0 && this.getCurrentGrowTicks() >= this.getTotalGrowTicks();
    }

    // Gives back a list of items that harvest will yield
    private NonNullList<ItemStack> createDropsList(){
        NonNullList<ItemStack> drops = NonNullList.create();
        for(LootData loot : this.entity.getResults()) {
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
            if(this.world != null && this.world.rand.nextFloat() <= loot.getChance()) {
                // Roll the amount of items
                int range = loot.getMaxAmount() - loot.getMinAmount() + 1;
                int amount = this.world.rand.nextInt(range) + loot.getMinAmount();
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
        if(this.world != null && !this.world.isRemote) {
            final double offsetX = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.15F;
            final double offsetY = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
            final double offsetZ = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.15F;
            final ItemEntity itemEntity = new ItemEntity(this.world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, item);
            itemEntity.setDefaultPickupDelay();
            this.world.addEntity(itemEntity);
        }
    }

    // NETWORKING

    @Override
    public CompoundNBT getUpdateTag(){
        CompoundNBT tag = super.getUpdateTag();
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
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet){
        CompoundNBT tag = packet.getNbtCompound();
        // Store old env and entity type
        ItemStack oldEnv = this.envItem;
        EntityType<?> oldEntityType = this.entityType;
        // Get upgrades
        this.cooking = tag.getBoolean("cooking");
        this.lightning = tag.getBoolean("lightning");
        this.arrow = tag.getBoolean("arrow");
        // Read the env
        this.envItem = ItemStack.read(tag.getCompound("environmentItem"));
        this.environment = MobCageTE.getEnvironmentFromItemStack(this.envItem);
        // Read the mob data
        this.entityType = SerializationHelper.deserializeEntityTypeNBT(tag);
        this.entity = MobCageTE.getMobDataFromType(this.entityType);
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
            this.totalGrowTicks = this.entity.getTotalGrowTicks();
        }
        // If env or entity changed, refresh model data
        if(!Objects.equals(oldEnv, this.envItem) || !Objects.equals(oldEntityType,this.entityType)){
            ModelDataManager.requestModelDataRefresh(this);
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    /// SERIALIZATION ///

    // Deserialize the block to read it from the drive
    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        // Call the parent
        super.read(state, nbt);
        // Read hopping and upgrades
        this.hopping = nbt.getBoolean("hopping");
        this.cooking = nbt.getBoolean("cooking");
        this.lightning = nbt.getBoolean("lightning");
        this.arrow = nbt.getBoolean("arrow");
        // Read the env
        this.envItem = ItemStack.read(nbt.getCompound("environmentItem"));
        this.environment = MobCageTE.getEnvironmentFromItemStack(this.envItem);
        // Read the mob data
        this.entityType = SerializationHelper.deserializeEntityTypeNBT(nbt);
        this.entity = MobCageTE.getMobDataFromType(this.entityType);
        // Read color
        this.color = nbt.getInt("color");
        // Read ticks info
        this.waitingForHarvest = nbt.getBoolean("waitingForHarvest");
        this.currentGrowTicks = nbt.getInt("currentGrowTicks");
        if(hasEntity()){
            this.totalGrowTicks = this.entity.getTotalGrowTicks();
        }
    }

    // Serialize the block to save it on drive
    @Override
    public CompoundNBT write(CompoundNBT dataTag) {
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
        return super.write(dataTag);
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

    public int getColor(){
        return this.color;
    }

    public void setCooking(boolean cooking) {
        this.cooking = cooking;
        // Sync with client
        if(this.world != null){
            this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public void setLightning(boolean lightning) {
        this.lightning = lightning;
        // Sync with client
        if(this.world != null){
            this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }

    public void setArrow(boolean arrow){
        this.arrow = arrow;
        // Sync with client
        if(this.world != null){
            this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        }
    }
}
