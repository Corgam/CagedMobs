package com.corgam.cagedmobs;

import com.corgam.cagedmobs.addons.theoneprobe.CagedMobsTOPSupport;
import com.corgam.cagedmobs.configs.ClientConfig;
import com.corgam.cagedmobs.configs.ServerConfig;
import com.corgam.cagedmobs.items.DnaSamplerDiamondItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.DnaSamplerNetheriteItem;
import com.corgam.cagedmobs.registers.*;
import com.corgam.cagedmobs.setup.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
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
        // Registries
        CagedBlocks.BLOCKS_REG.register(eventBus);
        CagedItems.ITEMS_REG.register(eventBus);
        CagedBlockEntities.TE_REG.register(eventBus);
        CagedCreativeTabs.CAGED_CREATIVE_TABS_REG.register(eventBus);
        CagedRecipeTypes.CAGED_RECIPE_TYPES.register(eventBus);
        CagedRecipeSerializers.CAGED_RECIPE_SERIALIZERS.register(eventBus);
        CagedContainers.CAGED_MENU_TYPES.register(eventBus);
        // Add properties to items
        eventBus.addListener(this::addPropertiesToItems);
        // TheOneProbe support
        eventBus.addListener(this::initTOPSupport);
    }

    /**
     * Adding properties to items with NBT to allow different textures based on nbt
     * @param event FMLClientSetupEvent event
     */
    private void addPropertiesToItems(final FMLClientSetupEvent event) {
        ItemProperties.register(CagedItems.DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity, unusedInt) -> DnaSamplerItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
        ItemProperties.register(CagedItems.DNA_SAMPLER_DIAMOND.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity, unusedInt) -> DnaSamplerDiamondItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
        ItemProperties.register(CagedItems.DNA_SAMPLER_NETHERITE.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity, unusedInt) -> DnaSamplerNetheriteItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
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
