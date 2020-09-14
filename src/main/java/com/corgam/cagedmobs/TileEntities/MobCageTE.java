package com.corgam.cagedmobs.TileEntities;

import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.SerializationHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.mob.LootData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.setup.CagedTE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import java.util.List;

public class MobCageTE extends TileEntity implements ITickableTileEntity {
    // Hopping
    boolean hopping = false;
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
    private boolean didAutoHarvest = false;

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

    public void onEnvironmentRemoval() {
        this.totalGrowTicks = 0;
        this.currentGrowTicks = 0;
        this.waitingForHarvest = false;
        this.environment = null;
        this.envItem = ItemStack.EMPTY;
        this.entity = null;
        this.entityType = null;
    }


/// ENTITY /////

    public MobData getEntity() {
        return entity;
    }

    public boolean hasEntity() {
        return entity != null;
    }

    public void setEntityFromType(EntityType<?> type) {
        this.markDirty();
        MobData mobData = getMobDataFromType(type);
        this.entity = mobData;
        this.entityType = type;
        this.totalGrowTicks = mobData.getTotalGrowTicks();
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
        this.entity = null;
        this.entityType = null;

        this.currentGrowTicks = 0;
        this.totalGrowTicks = 0;
        this.waitingForHarvest = false;
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
    }

    // Auto-harvests the cage, when there is a valid inv bellow
    private boolean autoHarvest() {
        final IItemHandler inventory = getInv(this.world, this.pos.down(), Direction.UP);
        if(inventory != EmptyHandler.INSTANCE && !this.world.isRemote){
            // For every item in drop list
           for(final ItemStack item : this.createDropsList()){
               // For every slot in inv
               for(int slot = 0; slot < inventory.getSlots(); slot++){
                   // Simulate the insert
                   if(inventory.isItemValid(slot, item) && inventory.insertItem(slot,item,true).getCount() != item.getCount()){
                    // Actual insert
                       inventory.insertItem(slot, item, false);
                       return true;
                   }
               }
           }
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
            if(this.world != null && this.world.rand.nextFloat() <= loot.getChance()) {
                // Roll the amount of items
                int range = loot.getMaxAmount() - loot.getMinAmount() + 1;
                int amount = this.world.rand.nextInt(range) + loot.getMinAmount();
                if(amount > 0) {
                    for(int i=0; i < amount ; i++) {
                        // Add copied item stack to the drop list
                        ItemStack stack = loot.getItem().copy();
                        // TODO Set count
                        drops.add(stack);
                    }
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

    /// SERIALIZATION ///

    // Deserialize the block to read it from the drive
    @Override
    public void func_230337_a_(BlockState state, CompoundNBT nbt) {
        // Call the parent
        super.func_230337_a_(state, nbt);
        // Read hopping
        this.hopping = nbt.getBoolean("hopping");
        // Read the env
        this.envItem = ItemStack.read(nbt.getCompound("environmentItem"));
        this.environment = MobCageTE.getEnvironmentFromItemStack(this.envItem);
        // Read the mob data
        this.entityType = SerializationHelper.deserializeEntityTypeNBT(nbt);
        this.entity = MobCageTE.getMobDataFromType(this.entityType);
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
        // Put hopping
        dataTag.putBoolean("hopping", this.hopping);
        // If cage has env, then put env info and maybe entity info
        if(this.hasEnvironment()) {
            // Put env info
            dataTag.put("environmentItem", this.envItem.serializeNBT());
            // If cage has entity, put entity info
            if(this.hasEntity()){
                // Put entity type
                SerializationHelper.serializeEntityTypeNBT(dataTag, this.entityType);
                // Put ticks info
                dataTag.putInt("currentGrowTicks", this.currentGrowTicks);
                dataTag.putBoolean("waitingForHarvest", this.waitingForHarvest);
            }
        }
        return super.write(dataTag);
    }
}
