package com.corgam.cagedmobs;

import com.corgam.cagedmobs.addons.theoneprobe.CagedMobsTOPSupport;
import com.corgam.cagedmobs.configs.ClientConfig;
import com.corgam.cagedmobs.configs.ServerConfig;
import com.corgam.cagedmobs.items.DnaSamplerDiamondItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.DnaSamplerNetheriteItem;
import com.corgam.cagedmobs.registers.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CagedMobs.MOD_ID)
public class CagedMobs
{
    // Mod ID
    public static final String MOD_ID = "cagedmobs";
    // Logger
    public static final Logger LOGGER = LogManager.getLogger();
    // Configs
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public CagedMobs(IEventBus modEventBus) {
        // Register client rendering
        modEventBus.addListener(ClientSetup::renderLayerSetup);
        // Register configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getForgeConfigSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getForgeConfigSpec());
        // Register deferred registries
        CagedBlocks.CAGED_BLOCKS_REGISTER.register(modEventBus);
        CagedItems.CAGED_ITEMS_REGISTER.register(modEventBus);
        CagedBlockEntities.CAGED_BLOCK_ENTITIES_REGISTER.register(modEventBus);
        CagedCreativeTabs.CAGED_CREATIVE_TABS_REGISTER.register(modEventBus);
        CagedRecipeTypes.CAGED_RECIPE_TYPES_REGISTER.register(modEventBus);
        CagedRecipeSerializers.CAGED_RECIPE_SERIALIZERS_REGISTER.register(modEventBus);
        CagedContainers.CAGED_MENU_TYPES_REGISTER.register(modEventBus);
        // Add properties to items
        modEventBus.addListener(this::addPropertiesToItems);
        // TheOneProbe support
        modEventBus.addListener(this::initTOPSupport);
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
