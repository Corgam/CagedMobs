package com.corgam.cagedmobs;

import com.corgam.cagedmobs.addons.theoneprobe.CagedMobsTOPSupport;
import com.corgam.cagedmobs.configs.ClientConfig;
import com.corgam.cagedmobs.configs.ServerConfig;
import com.corgam.cagedmobs.items.DnaSamplerDiamondItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.DnaSamplerNetheriteItem;
import com.corgam.cagedmobs.registers.*;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
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
        CagedBlocks.BLOCKS_REG.register(eventBus);
        CagedItems.ITEMS_REG.register(eventBus);
        CagedBlockEntities.TE_REG.register(eventBus);
//        CagedRecipeTypes.CAGED_RECIPE_TYPES.register(eventBus);
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
        ItemModelsProperties.register(CagedItems.DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> DnaSamplerItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
        ItemModelsProperties.register(CagedItems.DIAMOND_DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> DnaSamplerDiamondItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
        ItemModelsProperties.register(CagedItems.NETHERITE_DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> DnaSamplerNetheriteItem.containsEntityType(itemStack) ? 1.0F : 0.0F);
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
