package com.corgam.cagedmobs;

import com.corgam.cagedmobs.addons.theoneprobe.CagedMobsTOPSupport;
import com.corgam.cagedmobs.configs.ClientConfig;
import com.corgam.cagedmobs.configs.ServerConfig;
import com.corgam.cagedmobs.items.DnaSamplerDiamondItem;
import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.items.DnaSamplerNetheriteItem;
import com.corgam.cagedmobs.registers.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
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
        CagedRecipeTypes.CAGED_RECIPE_TYPES.register(eventBus);
        CagedRecipeSerializers.CAGED_RECIPE_SERIALIZERS.register(eventBus);
        CagedContainers.CAGED_MENU_TYPES.register(eventBus);
        // Add properties to items
        eventBus.addListener(this::addPropertiesToItems);
        // TheOneProbe support
        eventBus.addListener(this::initTOPSupport);
        // Creative tab
        eventBus.addListener(this::registerCagedMobsCreativeTab);
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

    public void registerCagedMobsCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(MOD_ID,"cagedmobs_tab"), builder ->
                // Set name of tab to display
                builder.title(Component.translatable("itemGroup.cagedmobs_tab"))
                        .icon(() -> new ItemStack(CagedItems.HOPPING_MOB_CAGE.get()))
                        .displayItems(((pParameters, pOutput) -> {
                            // Cages
                            pOutput.accept(CagedItems.MOB_CAGE.get());
                            pOutput.accept(CagedItems.HOPPING_MOB_CAGE.get());
                            // Samplers
                            pOutput.accept(CagedItems.DNA_SAMPLER.get());
                            pOutput.accept(CagedItems.DIAMOND_DNA_SAMPLER.get());
                            pOutput.accept(CagedItems.NETHERITE_DNA_SAMPLER.get());
                            // Upgrades
                            pOutput.accept(CagedItems.SPEED_I_UPGRADE.get());
                            pOutput.accept(CagedItems.SPEED_II_UPGRADE.get());
                            pOutput.accept(CagedItems.SPEED_III_UPGRADE.get());
                            pOutput.accept(CagedItems.FORTUNE_UPGRADE.get());
                            pOutput.accept(CagedItems.ARROW_UPGRADE.get());
                            pOutput.accept(CagedItems.COOKING_UPGRADE.get());
                            pOutput.accept(CagedItems.LIGHTNING_UPGRADE.get());
                            pOutput.accept(CagedItems.EXPERIENCE_UPGRADE.get());
                            pOutput.accept(CagedItems.CREATIVE_UPGRADE.get());
                            // Drop Items
                            pOutput.accept(CagedItems.DRAGON_SCALE.get());
                            pOutput.accept(CagedItems.NETHER_STAR_FRAGMENT.get());
                            pOutput.accept(CagedItems.WARDEN_RECEPTOR.get());
                            pOutput.accept(CagedItems.SPONGE_FRAGMENT.get());
                            pOutput.accept(CagedItems.HONEY_DROP.get());
                            pOutput.accept(CagedItems.MILK_DROP.get());
                            pOutput.accept(CagedItems.EXPERIENCE_ORB.get());
                            // Star-infused Netherite
                            pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_NUGGET.get());
                            pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_INGOT.get());
                            pOutput.accept(CagedItems.STAR_INFUSED_NETHERITE_BLOCK.get());
                            // Empty spawn egg
                            pOutput.accept(CagedItems.EMPTY_SPAWN_EGG.get());
                        })).build());
    }
}
