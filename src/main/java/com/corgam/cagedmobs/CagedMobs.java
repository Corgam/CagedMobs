package com.corgam.cagedmobs;

import com.corgam.cagedmobs.addons.theoneprobe.CagedMobsTOPSupport;
import com.corgam.cagedmobs.configs.ClientConfig;
import com.corgam.cagedmobs.configs.ServerConfig;
import com.corgam.cagedmobs.items.DnaSamplerDiamondItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.DnaSamplerNetheriteItem;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.env.EnvironmentDataSerializer;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootData;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootDataSerializer;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.serializers.mob.MobDataSerializer;
import com.corgam.cagedmobs.setup.*;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.profiler.IProfiler;

import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@Mod(Constants.MOD_ID)
public class CagedMobs
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final UUID CAGEDMOBS_UUID = UUID.nameUUIDFromBytes(Constants.MOD_ID.getBytes());
    final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public CagedMobs() {
        // Client
        eventBus.addListener(ClientSetup::renderLayerSetup);
        // Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getForgeConfigSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getForgeConfigSpec());
        // Recipes
        eventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
        // Registries
        CagedBlocks.BLOCKS_REG.register(eventBus);
        CagedItems.ITEMS_REG.register(eventBus);
        CagedTE.TE_REG.register(eventBus);
        // Add properties to items
        eventBus.addListener(this::addPropertiesToItems);
        // TheOneProbe support
        eventBus.addListener(this::initTOPSupport);
        // Register this for logging the recipes to the console
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        // Register new recipes
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(RecipesHelper.MOB_RECIPE.toString()), RecipesHelper.MOB_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(RecipesHelper.ENV_RECIPE.toString()), RecipesHelper.ENV_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(RecipesHelper.ADDITIONAL_LOOT_RECIPE.toString()), RecipesHelper.ADDITIONAL_LOOT_RECIPE);
        // Register recipe serializers
        event.getRegistry().register(EnvironmentDataSerializer.INSTANCE);
        event.getRegistry().register(MobDataSerializer.INSTANCE);
        event.getRegistry().register(AdditionalLootDataSerializer.INSTANCE);
    }

    // Adding properties to items with NBT to allow different textures based on nbt
    private void addPropertiesToItems(final FMLClientSetupEvent event) {
        ItemModelsProperties.register(CagedItems.DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> DnaSamplerItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
        ItemModelsProperties.register(CagedItems.DNA_SAMPLER_DIAMOND.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> DnaSamplerDiamondItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
        ItemModelsProperties.register(CagedItems.DNA_SAMPLER_NETHERITE.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> DnaSamplerNetheriteItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
    }
    // Initializes the TheOneProbe mod support
    private void initTOPSupport(final InterModEnqueueEvent event){
        if(ModList.get().isLoaded("theoneprobe")){
            InterModComms.sendTo("theoneprobe","getTheOneProbe", CagedMobsTOPSupport::new);
        }
    }
    // Logs to the console loaded recipes
    @SubscribeEvent(priority = EventPriority.LOW)
    public void resourceReload(AddReloadListenerEvent event) {
        event.addListener(new ReloadListener<Void>() {
            @Override
            @Nonnull
            @ParametersAreNonnullByDefault
            protected Void prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
                // MobData
                MobData.NUMBER_OF_LOADED_MOBDATA_RECIPES = 0;
                MobData.NUMBER_OF_NULL_MOBDATA_RECIPES = 0;
                // Additional loots
                AdditionalLootData.NUMBER_OF_LOADED_ADDITIONAL_LOOTDATA_RECIPES = 0;
                AdditionalLootData.NUMBER_OF_NULL_ADDITIONAL_LOOTDATA_RECIPES = 0;
                // Environments
                EnvironmentData.NUMBER_OF_LOADED_ENVIRONMENTDATA_RECIPES = 0;
                EnvironmentData.NUMBER_OF_NULL_ENVIRONMENTDATA_RECIPES = 0;
                return null;
            }
            @Override
            @ParametersAreNonnullByDefault
            protected void apply(Void objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
                // MobData recipes
                String msg = "[Caged Mobs] Loaded "+ MobData.NUMBER_OF_LOADED_MOBDATA_RECIPES + " MobData recipes (" + MobData.NUMBER_OF_NULL_MOBDATA_RECIPES + " null recipes)!";
                logMessage(new StringTextComponent(msg));
                // Additional loot recipes
                msg = "[Caged Mobs] Loaded "+ AdditionalLootData.NUMBER_OF_LOADED_ADDITIONAL_LOOTDATA_RECIPES + " AdditionalLootData recipes (" + AdditionalLootData.NUMBER_OF_NULL_ADDITIONAL_LOOTDATA_RECIPES + " null recipes)!";
                logMessage(new StringTextComponent(msg));
                // Environments recipes
                msg = "[Caged Mobs] Loaded "+ EnvironmentData.NUMBER_OF_LOADED_ENVIRONMENTDATA_RECIPES + " EnvironmentData recipes (" + EnvironmentData.NUMBER_OF_NULL_ENVIRONMENTDATA_RECIPES + " null recipes)!";
                logMessage(new StringTextComponent(msg));
            }
        });
    }
    // Logs a message in all players' consoles
    private static void logMessage(ITextComponent msg) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if(server != null) {
            server.getPlayerList().broadcastMessage(msg, ChatType.SYSTEM, CAGEDMOBS_UUID);
        } else {
            System.out.println(msg.getString());
        }
    }
}
