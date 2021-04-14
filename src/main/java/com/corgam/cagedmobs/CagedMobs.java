package com.corgam.cagedmobs;

import com.corgam.cagedmobs.addons.theoneprobe.CagedMobsTOPSupport;
import com.corgam.cagedmobs.configs.ClientConfig;
import com.corgam.cagedmobs.configs.ServerConfig;
import com.corgam.cagedmobs.items.DnaSamplerDiamondItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.DnaSamplerNetheriteItem;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentDataSerializer;
import com.corgam.cagedmobs.serializers.mob.AdditionalLootDataSerializer;
import com.corgam.cagedmobs.serializers.mob.MobDataSerializer;
import com.corgam.cagedmobs.setup.*;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Constants.MOD_ID)
public class CagedMobs
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
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
        ItemModelsProperties.register(CagedItems.DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> {
            return DnaSamplerItem.containsEntityType(itemStack) ? 1.0F : 0.0F;
        });
        ItemModelsProperties.register(CagedItems.DNA_SAMPLER_DIAMOND.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> {
            return DnaSamplerDiamondItem.containsEntityType(itemStack) ? 1.0F : 0.0F;
        });
        ItemModelsProperties.register(CagedItems.DNA_SAMPLER_NETHERITE.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> {
            return DnaSamplerNetheriteItem.containsEntityType(itemStack) ? 1.0F : 0.0F;
        });
    }
    // Initializes the TheOneProbe mod support
    private void initTOPSupport(final InterModEnqueueEvent event){
        if(ModList.get().isLoaded("theoneprobe")){
            InterModComms.sendTo("theoneprobe","getTheOneProbe", CagedMobsTOPSupport::new);
        }
    }
}
