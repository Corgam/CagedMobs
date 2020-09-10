package com.corgam.cagedmobs;

import com.corgam.cagedmobs.items.DnaSamplerItem;
import com.corgam.cagedmobs.serializers.RecipesHelper;
import com.corgam.cagedmobs.serializers.env.EnvironmentData;
import com.corgam.cagedmobs.serializers.env.RecipeTypeEnvData;
import com.corgam.cagedmobs.serializers.mob.MobData;
import com.corgam.cagedmobs.serializers.mob.RecipeTypeMobData;
import com.corgam.cagedmobs.setup.*;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.corgam.cagedmobs.serializers.RecipesHelper.ENV_RECIPE;
import static com.corgam.cagedmobs.serializers.RecipesHelper.MOB_RECIPE;

@Mod(Constants.MOD_ID)
public class CagedMobs
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

    public CagedMobs() {
        // Client
        eventBus.addListener(this::onClientSetup);
        // Recipes
        eventBus.addGenericListener(IRecipeSerializer.class, this::registerRecipeSerializers);
        // Registries
        CagedBlocks.BLOCKS_REG.register(eventBus);
        CagedItems.ITEMS_REG.register(eventBus);
        CagedTE.TE_REG.register(eventBus);
        eventBus.addListener(this::addPropertiesToItems);
    }

    private void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        // Register new recipes
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(RecipesHelper.MOB_RECIPE.toString()), RecipesHelper.MOB_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(RecipesHelper.ENV_RECIPE.toString()), RecipesHelper.ENV_RECIPE);
        // Register recipe serializers
        event.getRegistry().register(EnvironmentData.SERIALIZER);
        event.getRegistry().register(MobData.SERIALIZER);
    }

    // Adding properties to items with NBT to allow different textures based on nbt
    private void addPropertiesToItems(final FMLClientSetupEvent event) {
        ItemModelsProperties.func_239418_a_(CagedItems.DNA_SAMPLER.get(), new ResourceLocation("cagedmobs:full"), (itemStack, clientWorld, livingEntity) -> {
            return DnaSamplerItem.containsEntityType(itemStack) ? 1.0F : 0.0F;
        });
    }
    // Set the render type of blocks
    private void onClientSetup(FMLClientSetupEvent event) {
        ClientSetup.renderLayerSetup();
    }
}
