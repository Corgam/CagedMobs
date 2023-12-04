package com.corgam.cagedmobs;

import com.corgam.cagedmobs.addons.theoneprobe.CagedMobsTOPSupport;
import com.corgam.cagedmobs.configs.ClientConfig;
import com.corgam.cagedmobs.configs.ServerConfig;
import com.corgam.cagedmobs.items.DnaSamplerDiamondItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.DnaSamplerNetheriteItem;
import com.corgam.cagedmobs.registers.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
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

@Mod(CagedMobs.MOD_ID)
public class CagedMobs
{
    // Mod ID
    public static final String MOD_ID = "cagedmobs";
    // Logger
    public static final Logger LOGGER = LogManager.getLogger();
    // Eventbus
    final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    // Configs
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public CagedMobs() {
        // Client
        eventBus.addListener(ClientSetup::renderLayerSetup);
        // Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getForgeConfigSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getForgeConfigSpec());
        // Registries
        CagedBlocks.CAGED_BLOCKS_REGISTER.register(eventBus);
        CagedItems.CAGED_ITEMS_REGISTER.register(eventBus);
        CagedBlockEntities.CAGED_BLOCK_ENTITIES_REGISTER.register(eventBus);
        CagedRecipeSerializers.CAGED_RECIPE_SERIALIZERS_REGISTER.register(eventBus);
        CagedContainers.CAGED_MENU_TYPES_REGISTER.register(eventBus);
        // Register recipe types
        eventBus.addGenericListener(RecipeSerializer.class, this::registerRecipeSerializers);
        // Add properties to items
        eventBus.addListener(this::addPropertiesToItems);
        // TheOneProbe support
        eventBus.addListener(this::initTOPSupport);
    }

    /**
     * Register custom recipe types
     */
    private void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        // Register new recipes
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CagedRecipeTypes.ENTITY_RECIPE.toString()), CagedRecipeTypes.ENTITY_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CagedRecipeTypes.ENVIRONMENT_RECIPE.toString()), CagedRecipeTypes.ENVIRONMENT_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE.toString()), CagedRecipeTypes.ADDITIONAL_LOOT_RECIPE);
    }

    /**
     * Adding properties to items with NBT to allow different textures based on nbt
     * @param event FMLClientSetupEvent event
     */
    private void addPropertiesToItems(final FMLClientSetupEvent event) {
        ItemProperties.register(CagedItems.DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity, unusedInt) -> DnaSamplerItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
        ItemProperties.register(CagedItems.DIAMOND_DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity, unusedInt) -> DnaSamplerDiamondItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
        ItemProperties.register(CagedItems.NETHERITE_DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity, unusedInt) -> DnaSamplerNetheriteItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
    }

    /**
     * Initializes the TheOneProbe mod support
     * @param event InterModEnqueueEvent event
     */
    private void initTOPSupport(final InterModEnqueueEvent event){
        if(ModList.get().isLoaded("theoneprobe")){
            InterModComms.sendTo("theoneprobe","getTheOneProbe", CagedMobsTOPSupport::new);
        }
    }
}
